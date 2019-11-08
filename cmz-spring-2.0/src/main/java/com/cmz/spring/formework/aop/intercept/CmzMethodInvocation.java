package com.cmz.spring.formework.aop.intercept;

import java.lang.reflect.Method;
import java.util.List;

import com.cmz.spring.formework.aop.aspect.CmzJoinPoint;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午4:02:08
 * @description 方法调用
 */
public class CmzMethodInvocation implements CmzJoinPoint {

	private Object proxy;
	private Method method;
	private Object target;
	private Class<?> targetClass;
	private Object[] arguments;
	private List<Object> interceptorsAndDynamicMethodMatchers;

	private int currentInterceptorIndex = -1;

	@Override
	public Method getMethod() {
		return this.method;
	}

	@Override
	public Object[] getArguments() {
		return this.arguments;
	}

	@Override
	public Object getThis() {
		return this.target;
	}

	public CmzMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass,
			List<Object> interceptorsAndDynamicMethodMatchers) {
		this.proxy = proxy;
		this.target = target;
		this.targetClass = targetClass;
		this.method = method;
		this.arguments = arguments;
		this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
	}

	public Object proceed() throws Throwable {
		if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return this.method.invoke(this.target, this.arguments);
		}
		Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers
				.get(++this.currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof CmzMethodInterceptor) {
			CmzMethodInterceptor mi = (CmzMethodInterceptor) interceptorOrInterceptionAdvice;
			return mi.invoke(this);
		} else {
			return proceed();
		}
	}

}
