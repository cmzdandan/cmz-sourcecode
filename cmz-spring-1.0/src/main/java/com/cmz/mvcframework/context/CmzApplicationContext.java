package com.cmz.mvcframework.context;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cmz.mvcframework.annotation.CmzAutowired;
import com.cmz.mvcframework.annotation.CmzController;
import com.cmz.mvcframework.annotation.CmzService;
import com.cmz.mvcframework.beans.CmzBeanFactory;
import com.cmz.mvcframework.beans.CmzBeanWrapper;
import com.cmz.mvcframework.beans.config.CmzBeanDefinition;
import com.cmz.mvcframework.beans.config.CmzBeanPostProcessor;
import com.cmz.mvcframework.beans.support.CmzBeanDefinitionReader;
import com.cmz.mvcframework.beans.support.CmzDefaultListableBeanFactory;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月8日 下午4:31:54
 * @description ApplicationContext<p>
 * 为了简单便于理解原理和思想，这里直接把ApplicationContext写成一个实现类而不是一个接口
 */
public class CmzApplicationContext extends CmzDefaultListableBeanFactory implements CmzBeanFactory {

	private String[] configLocations;

	private CmzBeanDefinitionReader reader;

	// 单例的IOC容器
	private Map<String, Object> singletonBeanCacheMap = new ConcurrentHashMap<>();

	// 通用的IOC容器
	private Map<String, CmzBeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

	public CmzApplicationContext(String... configLocations) {
		this.configLocations = configLocations;
		try {
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() throws Exception {
		// 1、定位，定位配置文件
		reader = new CmzBeanDefinitionReader(this.configLocations);
		// 2、加载配置文件，扫描相关的类，把它们封装成 BeanDefinition
		List<CmzBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
		// 3、注册，把配置信息放到容器里面(伪 IOC 容器)
		doRegisterBeanDefinition(beanDefinitions);
		// 4、把不是延时加载的类，提前初始化
		doAutowrited();
	}

	// 只处理非延时加载的情况
	private void doAutowrited() {
		for (Map.Entry<String, CmzBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
			String beanName = beanDefinitionEntry.getKey();
			if (!beanDefinitionEntry.getValue().getLazyInit()) {
				// 不是延迟加载，则调用getBean()方法
				try {
					getBean(beanName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void doRegisterBeanDefinition(List<CmzBeanDefinition> beanDefinitions) throws Exception {
		for (CmzBeanDefinition beanDefinition : beanDefinitions) {
			if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
				throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exists!");
			}
			super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
		}
		// 到这里为止，容器初始化完毕
	}

	/**
	 *  这里分了两个方法，先初始化，然后注入；不能放在一个方法里面搞定，这样设计是为了解决循环依赖的问题。
	 *  依赖注入，从这里开始，通过读取 BeanDefinition 中的信息<p>
	 *  然后，通过反射机制创建一个实例并返回<p>
	 *  Spring 做法是，不会把最原始的对象放出去，会用一个 BeanWrapper 来进行一次包装
	 *  装饰器模式:
	 *  (1).保留原来的 OOP 关系;
	 *  (2).我需要对它进行扩展，增强（为了以后 AOP 打基础）
	 */
	@Override
	public Object getBean(String beanName) throws Exception {
		CmzBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
		try {
			// 生成通知事件
			CmzBeanPostProcessor beanPostProcessor = new CmzBeanPostProcessor();
			Object instance = instantiateBean(beanDefinition);
			if (null == instance) {
				return null;
			}
			// 在实例初始化以前调用一次
			beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
			CmzBeanWrapper beanWrapper = new CmzBeanWrapper(instance);
			this.beanWrapperMap.put(beanName, beanWrapper);
			// 在实例初始化以后调用一次
			beanPostProcessor.postProcessAfterInitialization(instance, beanName);
			// 依赖注入
			populateBean(beanName, instance);
			// 通过这样一调用，相当于给我们自己留有了可操作的空间
			return this.beanWrapperMap.get(beanName).getWrappedInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getBean(Class<?> beanClass) throws Exception {
		return getBean(beanClass.getName());
	}

	// 依赖注入
	private void populateBean(String beanName, Object instance) {
		Class<?> clazz = instance.getClass();
		// 判断，只有加了注解的类，才需要进行依赖注入
		if (!(clazz.isAnnotationPresent(CmzController.class) || clazz.isAnnotationPresent(CmzService.class))) {
			return;
		}
		// 获取所有的fields，对加了Autowired注解的属性进行注入
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(CmzAutowired.class)) {
				continue;
			}
			CmzAutowired annotation = field.getAnnotation(CmzAutowired.class);
			String autowiredBeanName = annotation.value();
			autowiredBeanName = "".equals(autowiredBeanName) ? field.getType().getName() : autowiredBeanName;
			field.setAccessible(true);
			try {
				// 对属性进行注入值(对当前实例的当前属性注入值，该值从IOC容器中获取)
				field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	// 初始化Bean
	private Object instantiateBean(CmzBeanDefinition beanDefinition) {
		// 1.拿到要实例化的对象的类名
		String className = beanDefinition.getBeanClassName();
		// 2.反射实例化，得到一个对象
		Object instance = null;
		try {
			// 假设默认都是单例，暂时不考虑非常细的细节(根据 Class 才能确定一个类是否有实例)
			if (this.singletonBeanCacheMap.containsKey(className)) {
				instance = this.singletonBeanCacheMap.get(className);
			} else {
				Class<?> clazz = Class.forName(className);
				instance = clazz.newInstance();
				this.singletonBeanCacheMap.put(beanDefinition.getFactoryBeanName(), instance);
			}
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
