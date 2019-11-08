package com.cmz.mvcframework.beans.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.cmz.mvcframework.beans.config.CmzBeanDefinition;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月9日 上午9:47:59
 * @description BeanDefinitionReader
 */
public class CmzBeanDefinitionReader {

	private List<String> registyBeanClasses = new ArrayList<>();

	private Properties config = new Properties();

	// 要扫描的包的key，简化版本，这里就定义成一个常量，相当于xml规范
	private final String SCAN_PACKAGE = "scanPackage";

	public CmzBeanDefinitionReader(String... locations) {
		// 通过 URL 定位找到其所对应的文件，然后转换为文件流
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
		try {
			config.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 接下来根据配置信息扫描包
		doScaner(config.getProperty(SCAN_PACKAGE));
	}

	private void doScaner(String scanPackage) {
		// 转换为文件路径，实际上就是把.替换为/就 OK 了
		URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
		// URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
		File classPath = new File(url.getFile());
		for (File file : classPath.listFiles()) {
			if (file.isDirectory()) {
				// 遇到目录则递归
				doScaner(scanPackage + "." + file.getName());
			} else {
				if (!file.getName().endsWith(".class")) {
					// 不处理非 .class文件
					continue;
				}
				String className = (scanPackage + "." + file.getName().replace(".class", ""));
				registyBeanClasses.add(className);
			}

		}
	}

	// 对外提供一个获取解析到的配置信息对象的方法
	public Properties getConfig() {
		return this.config;
	}

	// 把配置文件中扫描到的所有配置信息转换为CmzBeanDefinition
	public List<CmzBeanDefinition> loadBeanDefinitions() {
		List<CmzBeanDefinition> result = new ArrayList<>();
		try {
			for (String className : registyBeanClasses) {
				Class<?> beanClass = Class.forName(className);
				if (beanClass.isInterface()) {
					continue;
				}
				String factoryBeanName = toLowerFirstCase(beanClass.getSimpleName());
				String beanClassName = beanClass.getName();
				CmzBeanDefinition beanDefinition = doCreateBeanDefinition(factoryBeanName, beanClassName);
				if (null == beanDefinition) {
					continue;
				}
				result.add(beanDefinition);
				Class<?> [] interfaces = beanClass.getInterfaces();
				for (Class<?> clazz : interfaces) {
					CmzBeanDefinition beanDefinition2 = doCreateBeanDefinition(clazz.getName(), beanClassName);
					result.add(beanDefinition2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 根据各个bean的名字，将其解析 为 BeanDefinition 对象
	private CmzBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
		CmzBeanDefinition beanDefinition = new CmzBeanDefinition();
		beanDefinition.setBeanClassName(beanClassName);
		beanDefinition.setFactoryBeanName(factoryBeanName);
		return beanDefinition;
	}

	// 将首字母转换为小写字母
	private String toLowerFirstCase(String simpleName) {
		char[] charArray = simpleName.toCharArray();
		if (charArray[0] >= 'A' && charArray[0] <= 'Z') {
			// 如果首字母是大写字母才做转换，否则不转换
			charArray[0] += 32;
		}
		return String.valueOf(charArray);
	}

}
