package com.cmz.mvcframework.beans;
/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月8日 下午4:28:24
 * @description 单例工厂的顶层设计
 */
public interface CmzBeanFactory {

	/**
	 * 根据beanName从IOC容器中获取一个实例bean
	 * @param beanName
	 * @return
	 * @throws Exception
	 */
	public Object getBean(String beanName) throws Exception;
	
	/**
	 * 根据beanClass从IOC容器中获取一个实例bean
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public Object getBean(Class<?> beanClass) throws Exception;
	
}
