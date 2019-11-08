package com.cmz.mvcframework.beans;
/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月9日 下午12:01:23
 * @description BeanWrapper(包装器模式)，解析完了并且实例化之后的Bean都包装到这个里面
 */
public class CmzBeanWrapper {

	private Object wrappedInstance;
	private Class<?> wrappedClass;
	
	public CmzBeanWrapper(Object wrappedInstance) {
		this.wrappedInstance = wrappedInstance;
	}
	
	public Object getWrappedInstance() {
		return this.wrappedInstance;
	}

	// 返回代理以后的Class，可能会是一个  $Proxy0
	public Class<?> getWrappedClass() {
		return this.wrappedInstance.getClass();
	}
	
}
