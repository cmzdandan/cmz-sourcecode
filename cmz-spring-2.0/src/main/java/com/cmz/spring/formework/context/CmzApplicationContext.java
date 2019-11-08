package com.cmz.spring.formework.context;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.cmz.spring.formework.annotation.CmzAutowired;
import com.cmz.spring.formework.annotation.CmzController;
import com.cmz.spring.formework.annotation.CmzService;
import com.cmz.spring.formework.aop.CmzAopConfig;
import com.cmz.spring.formework.aop.CmzAopProxy;
import com.cmz.spring.formework.aop.CmzCglibAopProxy;
import com.cmz.spring.formework.aop.CmzJdkDynamicAopProxy;
import com.cmz.spring.formework.aop.support.CmzAdvisedSupport;
import com.cmz.spring.formework.beans.CmzBeanWrapper;
import com.cmz.spring.formework.beans.config.CmzBeanDefinition;
import com.cmz.spring.formework.beans.config.CmzBeanPostProcessor;
import com.cmz.spring.formework.context.support.CmzBeanDefinitionReader;
import com.cmz.spring.formework.context.support.CmzDefaultListableBeanFactory;
import com.cmz.spring.formework.core.CmzBeanFactory;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 下午12:01:51
 * @description IOC 、 DI 、 MVC 、 AOP
 */
public class CmzApplicationContext extends CmzDefaultListableBeanFactory implements CmzBeanFactory {

	private String[] configLoactions;
	private CmzBeanDefinitionReader reader;

	/**
	 * 单例的 IOC 容器缓存(key:全类名，value：类的实例对象)
	 */
	private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

	// 通用的 IOC 容器
	private Map<String, CmzBeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

	public CmzApplicationContext(String... configLoactions) {
		this.configLoactions = configLoactions;
		try {
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() throws Exception {
		// 1.定位(定位配置文件，让reader读取配置文件中配置的包下的所有类的全类名放到一个集合中)
		reader = new CmzBeanDefinitionReader(this.configLoactions);
		// 2.加载(通过读取的全类名集合，加载出，也就是解析出，也就是封装出所有的BeanDenifition)
		List<CmzBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
		// 3注册(把所有的BeanDenifition放进IOC容器)
		doRegisterBeanDefinition(beanDefinitions);

	}

	/**
	 * 将所有的 beanDefinition 放入到 beanDefinitionMap 中
	 * <p>
	 * key:factoryBeanName 类名首字母小写
	 * <p>
	 * value:beanDefinition 包下的类解析为BeanDefinition对象
	 * 
	 * @param beanDefinitions
	 * @throws Exception
	 */
	private void doRegisterBeanDefinition(List<CmzBeanDefinition> beanDefinitions) throws Exception {
		for (CmzBeanDefinition beanDefinition : beanDefinitions) {
			if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
				// beanDefinitionMap 中以 类名小写作为key，即 factoryBeanName 作为key, beanDefinition
				// 作为value
				throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
			}
			super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
		}
		// 到此，容器初始化完毕(此时，并没有开始DI)
	}

	/**
	 * 根据类对象获取bean的实例对象
	 */
	@Override
	public Object getBean(Class<?> beanClass) throws Exception {
		return getBean(beanClass.getName());
	}

	/**
	 * 根据全类名获取bean的实例对象
	 * <p>
	 * 依赖注入，从这里开始，通过读取 BeanDefinition 中的信息，然后，通过反射机制创建一个实例并返回
	 * <p>
	 * Spring 做法是，不会把最原始的对象放出去，会用一个 BeanWrapper 来进行一次包装
	 * <p>
	 * 装饰器模式：
	 * <p>
	 * 1、保留原来的 OOP 关系
	 * <p>
	 * 2、我需要对它进行扩展，增强（为了以后 AOP 打基础）
	 * <p>
	 * 这里的getBean()相当于我们之前使用过的：
	 * <p>
	 * HelloService helloService = applicationContext.getBean("helloServiceImpl");
	 * 
	 * @param beanName
	 *            类名首字母小写
	 */
	@Override
	public Object getBean(String beanName) throws Exception {
		CmzBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
		try {
			Object instance = instantiateBean(beanDefinition);
			if (null == instance) {
				return null;
			}
			// 生成通知事件
			CmzBeanPostProcessor beanPostProcessor = new CmzBeanPostProcessor();
			// 在实例初始化以前调用一次
			beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
			CmzBeanWrapper beanWrapper = new CmzBeanWrapper(instance);
			this.beanWrapperMap.put(beanName, beanWrapper);
			// 在实例初始化以后调用一次
			beanPostProcessor.postProcessAfterInitialization(instance, beanName);
			// 对属性进行依赖注入
			populateBean(beanName, instance);
			// 通过这样一调用，相当于给我们自己留有了可操作的空间
			return this.beanWrapperMap.get(beanName).getWrappedInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 填充bean(对@Autowired修饰的属性进行依赖注入)
	 * 
	 * @param beanName
	 * @param instance
	 */
	private void populateBean(String beanName, Object instance) {
		Class<?> clazz = instance.getClass();
		if (!(clazz.isAnnotationPresent(CmzController.class) || clazz.isAnnotationPresent(CmzService.class))) {
			// 如果类的头上不是 @CmzController，也不是 @CmzService 修饰的，则无需做自动注入处理
			return;
		}
		// 如果这里直接是 getFields() 很有可能拿不到值，因为很多field都设置为了private
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(CmzAutowired.class)) {
				continue;
			}
			// 拿到字段的注解对象
			CmzAutowired annotation = field.getAnnotation(CmzAutowired.class);
			// 拿到注解的别名(用户自定义的属性名称，如果没有则用字段名)
			String autowiredBeanName = annotation.value().trim();
			if ("".equals(autowiredBeanName)) {
				// 如果用户没有自定义该字段的别名，则使用该字段的类型的全类名作为 beanName
				// 例如：这里是 @Autowired HelloService helloService;
				// field.getType() 得到 HelloService.class, 再 getName() 得到
				// com.cmz.service.HelloService
				autowiredBeanName = field.getType().getName();
			}
			field.setAccessible(true);
			try {
				Object value = this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance();
				field.set(instance, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 传一个 BeanDefinition，就返回一个实例 Bean
	 * 改造后的 instantiateBean() 方法，在生成实例Bean后，对实例Bean进行切面增强
	 * 
	 * @param beanDefinition
	 * @return 实例 Bean
	 */
	private Object instantiateBean(CmzBeanDefinition beanDefinition) {
		Object instance = null;
		// 拿到类的全类名
		String beanClassName = beanDefinition.getBeanClassName();
		try {
			if (this.singletonObjects.containsKey(beanClassName)) {
				instance = this.singletonObjects.get(beanClassName);
			} else {
				Class<?> clazz = Class.forName(beanClassName);
				instance = clazz.newInstance();
				// 这里进行aop
				CmzAdvisedSupport config = instantionAopConfig(beanDefinition);
				config.setTargetClass(clazz);
				config.setTarget(instance);
				if (config.pointCutMatch()) {
					instance = createProxy(config).getProxy();
				}
				this.singletonObjects.put(beanClassName, instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	/**
	 * 创建动态代理
	 * 
	 * @param config
	 * @return
	 */
	private CmzAopProxy createProxy(CmzAdvisedSupport config) {
		Class<?> targetClass = config.getTargetClass();
		if (targetClass.getInterfaces().length > 0) {
			return new CmzJdkDynamicAopProxy(config);
		}
		return new CmzCglibAopProxy(config);
	}

	/**
	 * 初始化切面通知支持
	 * 
	 * @param beanDefinition
	 * @return
	 */
	private CmzAdvisedSupport instantionAopConfig(CmzBeanDefinition beanDefinition) {
		CmzAopConfig config = new CmzAopConfig();
		config.setPointCut(reader.getConfig().getProperty("pointCut"));
		config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
		config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
		config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
		config.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
		config.setAspectAfterThrowingName(reader.getConfig().getProperty("aspectAfterThrowingName"));
		return new CmzAdvisedSupport(config);
	}

	/**
	 * 获取所有的 BeanDefinition 的名字(数组)
	 * 
	 * @return
	 */
	public String[] getBeanDefinitionNames() {
		return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
	}

	/**
	 * 获取BeanDefinition的总数量
	 * 
	 * @return
	 */
	public int getBeanDefinitionCount() {
		return this.beanDefinitionMap.size();
	}

	/**
	 * 获取配置信息(将配置信息解析到了一个Properties对象中)
	 * 
	 * @return
	 */
	public Properties getConfig() {
		return this.reader.getConfig();
	}
}
