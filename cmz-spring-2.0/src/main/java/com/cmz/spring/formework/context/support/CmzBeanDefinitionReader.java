package com.cmz.spring.formework.context.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.cmz.spring.formework.beans.config.CmzBeanDefinition;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 下午12:04:30
 * @description 对配置文件进行查找，读取、解析(解析成为BeanDefinition对象)
 */
public class CmzBeanDefinitionReader {

	private List<String> registyBeanClasses = new ArrayList<>();

	private Properties config = new Properties();

	// 固定配置文件中的 key，相对于 xml 的规范
	private final String SCAN_PACKAGE = "scanPackage";

	/**
	 * 将配置文件读取到 Properties 对象中
	 * 
	 * @param locations
	 */
	public CmzBeanDefinitionReader(String... locations) {
		// 通过 URL 定位找到其所对应的文件，然后转换为文件流
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(locations[0].replace("classpath:", ""));
		try {
			config.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		doScanner(config.getProperty(SCAN_PACKAGE));
	}

	/**
	 * 扫描配置的包下路径，将类文件的名字保存到集合中 registyBeanClasses
	 * 
	 * @param property
	 */
	private void doScanner(String scanPackage) {
		URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll(".", "/"));
		File classPath = new File(url.getFile());
		for (File file : classPath.listFiles()) {
			if (file.isDirectory()) {
				doScanner(scanPackage + "." + file.getName());
			} else {
				if (file.getName().endsWith(".class")) {
					String className = scanPackage + "." + file.getName().replace(".class", "");
					registyBeanClasses.add(className);
				}
			}
		}
	}

	/**
	 * 返回配置信息
	 * 
	 * @return
	 */
	public Properties getConfig() {
		return this.config;
	}

	/**
	 * 把配置文件中扫描到的所有的配置信息转换为 GPBeanDefinition 对象，以便于之后 IOC 操作方便
	 * @return
	 */
	public List<CmzBeanDefinition> loadBeanDefinitions() {
		List<CmzBeanDefinition> result = new ArrayList<>();
		try {
			for (String beanClass : registyBeanClasses) {
				Class<?> clazz = Class.forName(beanClass);
				if(clazz.isInterface()) {
					continue;
				}
				// 将类名的首字母转换为小写，作为factoryBeanName
				String factoryBeanName = toLowerFirstCase(clazz.getSimpleName());
				String beanClassName = clazz.getName();
				CmzBeanDefinition cmzBeanDefinition = doCreateBeanDefinition(factoryBeanName, beanClassName);
				result.add(cmzBeanDefinition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 把每一个配信息解析成一个 BeanDefinition
	 * @param factoryBeanName 类名(首字母小写，例如：helloServiceImpl)
	 * @param beanClassName 全类名(例如：com.cmz.service.impl.HelloServiceImpl)
	 * @return
	 */
	private CmzBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
		CmzBeanDefinition cmzBeanDefinition = new CmzBeanDefinition();
		cmzBeanDefinition.setFactoryBeanName(factoryBeanName);
		cmzBeanDefinition.setBeanClassName(beanClassName);
		return cmzBeanDefinition;
	}

	/**
	 * 将字符串的首字母转换为 小写
	 * @param simpleName 类名
	 * @return
	 */
	private String toLowerFirstCase(String simpleName) {
		char[] charArray = simpleName.toCharArray();
		if(charArray[0] >= 'A' && charArray[0] <= 'Z') {
			charArray[0] += 32;
		}
		return String.valueOf(charArray);
	}

}
