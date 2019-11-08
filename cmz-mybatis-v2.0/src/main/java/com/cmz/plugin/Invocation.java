package com.cmz.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午9:58:36
 * @description 包装类，对所有的被代理对象进行包装
 */
public class Invocation {
	
	private Object target;
	private Method method;
	private Object[] args;
	
	public Invocation(Object target, Method method, Object[] args) {
		this.target = target;
		this.method = method;
		this.args = args;
	}

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}
	
	public Object proceed() throws InvocationTargetException, IllegalAccessException {
		return method.invoke(target, args);
	}

}
