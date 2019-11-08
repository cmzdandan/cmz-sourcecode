package com.cmz.spring.formework.context.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cmz.spring.formework.beans.config.CmzBeanDefinition;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 上午11:58:28
 * @description 默认的IOC容器:beanDefinitionMap
 */
public class CmzDefaultListableBeanFactory extends CmzAbstractApplicationContext {

	/**
	 * 存储注册信息的 BeanDefinition
	 */
	protected final Map<String, CmzBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

}
