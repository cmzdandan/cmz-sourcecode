package com.cmz.mvcframework.beans.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cmz.mvcframework.beans.config.CmzBeanDefinition;
import com.cmz.mvcframework.context.support.CmzAbstractApplicationContext;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月8日 下午4:43:52
 * @description DefaultListableBeanFactory
 */
public class CmzDefaultListableBeanFactory extends CmzAbstractApplicationContext {

	//存储注册信息的BeanDefinition(这里是一个伪IOC容器，真正的IOC容器存储 BeanWrapper)
	protected final Map<String, CmzBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
	
}
