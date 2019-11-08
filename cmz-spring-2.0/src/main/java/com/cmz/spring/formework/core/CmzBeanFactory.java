package com.cmz.spring.formework.core;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 上午11:37:50
 * @description BeanFactory
 */
public interface CmzBeanFactory {

	/**
	 * 根据beanName从IOC容器中获取一个实例bean
	 * 
	 * @param beanName
	 * @return
	 * @throws Exception
	 */
	public Object getBean(String beanName) throws Exception;

	/**
	 * 根据beanClass从IOC容器中获取一个实例bean
	 * 
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public Object getBean(Class<?> beanClass) throws Exception;

}
