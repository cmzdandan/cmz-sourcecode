package com.cmz.mvcframework.beans.config;

import lombok.Data;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月8日 下午4:50:23
 * @description BeanDefinition
 * <p>在Spring中，这个类是一个 接口，为了扩展各种各样的BeanDefinition出来</p>
 * <p>我们这里不搞那么复杂，直接写成一个实现类</p>
 */
@Data
public class CmzBeanDefinition {

	private String beanClassName;
	private Boolean lazyInit = false;
	private String factoryBeanName;
	private Boolean isSingleton = true;

	public String getBeanClassName() {
		return beanClassName;
	}

	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public Boolean getLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(Boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	public String getFactoryBeanName() {
		return factoryBeanName;
	}

	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

	public Boolean getIsSingleton() {
		return isSingleton;
	}

	public void setIsSingleton(Boolean isSingleton) {
		this.isSingleton = isSingleton;
	}

}
