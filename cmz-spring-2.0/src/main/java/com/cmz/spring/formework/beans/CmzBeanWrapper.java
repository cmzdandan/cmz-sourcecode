package com.cmz.spring.formework.beans;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 上午11:52:03
 * @description 真正的封装bean内容的载体
 */
public class CmzBeanWrapper {

	private Object wrappedInstance;
	private Class<?> wrappedClass;

	public CmzBeanWrapper(Object wrappedInstance) {
		this.wrappedInstance = wrappedInstance;
	}

	public Object getWrappedInstance() {
		return wrappedInstance;
	}

	/**
	 * 返回代理以后的Class，可能会是一个 $Proxy0
	 * @return
	 */
	public Class<?> getWrappedClass() {
		return wrappedInstance.getClass();
	}

}
