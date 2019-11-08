package com.cmz.spring.formework.beans.config;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 上午11:48:56
 * @description 用来存储配置文件中的信息,相当于保存在内存中的配置
 */
public class CmzBeanDefinition {

	// 全类名(例如：com.cmz.service.impl.HelloServiceImpl)
	private String beanClassName;
	
	// 类名(首字母小写，例如：helloServiceImpl)
	private String factoryBeanName;
	
	// 是否懒加载
	private boolean lazyInit = false;

	public String getBeanClassName() {
		return beanClassName;
	}

	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public boolean isLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	public String getFactoryBeanName() {
		return factoryBeanName;
	}

	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

}
