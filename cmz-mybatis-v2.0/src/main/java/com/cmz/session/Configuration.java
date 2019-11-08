package com.cmz.session;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.cmz.MyBatisBootstrap;
import com.cmz.annotation.Entity;
import com.cmz.annotation.Select;
import com.cmz.binding.MapperRegistry;
import com.cmz.executor.CachingExecutor;
import com.cmz.executor.Executor;
import com.cmz.executor.SimpleExecutor;
import com.cmz.plugin.Interceptor;
import com.cmz.plugin.InterceptorChain;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午10:20:18
 * @description 全局配置类
 */
public class Configuration {

	// SQL映射关系配置，使用注解时不用重复配置
	public static final ResourceBundle sqlMappings;

	// 全局配置
	public static final ResourceBundle properties;

	// 维护接口与工厂类关系
	public static final MapperRegistry MAPPER_REGISTRY = new MapperRegistry();

	// 维护接口方法与SQL关系
	public static final Map<String, String> mappedStatements = new HashMap<>();

	// 插件
	private InterceptorChain interceptorChain = new InterceptorChain();

	// 所有Mapper接口
	private List<Class<?>> mapperList = new ArrayList<>();

	// 所有类文件
	private List<String> classPaths = new ArrayList<>();

	static {
		sqlMappings = ResourceBundle.getBundle("sql");
		properties = ResourceBundle.getBundle("mybatis");
	}

	/**
	 * 初始化时解析全局配置文件
	 */
	public Configuration() {
		// Note：注解中配置的SQL会覆盖 properties文件中配置的SQL
		// 1.解析sql.properties
		for (String key : sqlMappings.keySet()) {
			Class<?> mapper = null;
			String statementId = null;
			String pojoStr = null;
			Class<?> pojo = null;
			// properties中的value用--隔开，第一个是SQL语句
			statementId = sqlMappings.getString(key).split("--")[0];
			// properties中的value用--隔开，第二个是需要转换的POJO类型
			pojoStr = sqlMappings.getString(key).split("--")[1];
			try {
				// properties中的key是接口类型+方法,从接口类型+方法中截取接口类型
				mapper = Class.forName(key.substring(0, key.lastIndexOf(".")));
				pojo = Class.forName(pojoStr);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			// 维护接口与返回的实体类映射关系
			MAPPER_REGISTRY.addMapper(mapper, pojo);
			// 维护接口方法与SQL映射关系
			mappedStatements.put(key, statementId);
		}
		
		// 2.解析Mapper接口配置，扫描注册
		String mapperPath = properties.getString("mapper.path");
		scanPackage(mapperPath);
		for(Class<?> mapper : mapperList) {
			// 主要做两件事：(1).注册接口与实体类的映射关系；(2).注册接口类型+方法名和SQL语句的映射关系
			parsingClass(mapper);
		}
		// 3.解析插件，可配置多个插件(用逗号隔开)
		String pluginPathValue = properties.getString("plugin.path");
		String[] pluginPaths = pluginPathValue.split(",");
		if(pluginPaths != null) {
			// 将插件添加到interceptorChain中
			for(String plugin : pluginPaths) {
				Interceptor interceptor = null;
				try {
					interceptor = (Interceptor) Class.forName(plugin).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				interceptorChain.addInterceptor(interceptor);
			}
		}
	}

	/**
	 * 根据statementId判断是否存在映射的SQL
	 * 
	 * @param statementId
	 * @return
	 */
	public boolean hasStatement(String statementId) {
		return mappedStatements.containsKey(statementId);
	}

	/**
	 * 获取接口对象
	 * 
	 * @param clazz
	 * @param sqlSession
	 * @return
	 */
	public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {
		return MAPPER_REGISTRY.getMapper(clazz, sqlSession);
	}

	/**
	 * 根据statementId获取SQL
	 * 
	 * @param statementId
	 * @return
	 */
	public String getMappedStatement(String statementId) {
		return mappedStatements.get(statementId);
	}

	/**
	 * 创建执行器
	 * <p>
	 * 当开启缓存时，使用缓存装饰
	 * <p>
	 * 当配置插件时，使用插件代理
	 * 
	 * @return
	 */
	public Executor newExecutor() {
		Executor executor = null;
		if (properties.getString("cache.enabled").equals("true")) {
			executor = new CachingExecutor(new SimpleExecutor());
		} else {
			executor = new SimpleExecutor();
		}
		// 目前只拦截了Executor，所有的插件都对Executor进行代理，没有对拦截类和方法签名进行判断
		if (interceptorChain.hasPlugin()) {
			executor = (Executor) interceptorChain.pluginAll(executor);
		}
		return executor;
	}

	/**
	 * 解析Mapper接口上配置的注解（SQL语句）
	 * 
	 * @param mapper
	 */
	private void parsingClass(Class<?> mapper) {
		// 1.解析类上的注解
		// 如果有@Entity注解，说明是查询数据库的接口
		if (mapper.isAnnotationPresent(Entity.class)) {
			for (Annotation annotation : mapper.getAnnotations()) {
				if (annotation.annotationType().equals(Entity.class)) {
					// 注册接口与实体类的映射关系
					MAPPER_REGISTRY.addMapper(mapper, ((Entity) annotation).value());
				}
			}
		}

		// 2.解析方法上的注解
		Method[] methods = mapper.getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Select.class)) {
				for (Annotation annotation : method.getDeclaredAnnotations()) {
					if (annotation.annotationType().equals(Select.class)) {
						// 注册接口类型+方法名和SQL语句的映射关系
						String statementId = method.getDeclaringClass().getName() + "." + method.getName();
						mappedStatements.put(statementId, ((Select) annotation).value());
					}
				}
			}
		}
	}

	/**
	 * 根据全局配置文件的Mapper接口路径，扫描所有接口
	 * 
	 * @param mapperPath
	 */
	private void scanPackage(String mapperPath) {
		String classPath = MyBatisBootstrap.class.getResource("/").getPath();
		mapperPath = classPath.replace(".", File.separator);
		String mainPath = classPath + mapperPath;
		scanClassFile(new File(mainPath));
		// 处理所有的类文件(字节码文件)
		for (String className : classPaths) {
			className = className.replace(classPath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\", ".")
					.replace(".class", "");
			Class<?> clazz = null;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (clazz.isInterface()) {
				mapperList.add(clazz);
			}
		}
	}

	/**
	 * 扫描 字节码文件
	 * 
	 * @param file
	 */
	private void scanClassFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileSub : files) {
				scanClassFile(fileSub);
			}
		} else {
			// 如果是 .class 文件则直接添加
			if (file.getName().endsWith(".class")) {
				classPaths.add(file.getPath());
			}
		}
	}

}
