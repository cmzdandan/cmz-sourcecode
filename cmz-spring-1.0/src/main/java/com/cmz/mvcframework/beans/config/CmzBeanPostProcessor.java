package com.cmz.mvcframework.beans.config;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月9日 下午6:30:11
 * @description BeanPostProcessor 
 */
public class CmzBeanPostProcessor {

	// 为在 Bean 的初始化前提供回调入口
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
		return bean;
	}

	// 为在 Bean 的初始化之后提供回调入口
	public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
		return bean;
	}

}
