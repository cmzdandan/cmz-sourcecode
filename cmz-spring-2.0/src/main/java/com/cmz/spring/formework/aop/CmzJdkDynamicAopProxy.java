package com.cmz.spring.formework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.cmz.spring.formework.aop.intercept.CmzMethodInvocation;
import com.cmz.spring.formework.aop.support.CmzAdvisedSupport;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 上午11:25:02
 * @description jdk动态代理
 */
public class CmzJdkDynamicAopProxy implements CmzAopProxy, InvocationHandler {

	private CmzAdvisedSupport config;

	public CmzJdkDynamicAopProxy(CmzAdvisedSupport config) {
		this.config = config;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		List<Object> interceptorsAndDynamicMethodMatchers = config.getInterceptorsAndDynamicInterceptionAdvice(method,
				this.config.getTargetClass());
		CmzMethodInvocation invocation = new CmzMethodInvocation(proxy, this.config.getTarget(), method, args,
				this.config.getTargetClass(), interceptorsAndDynamicMethodMatchers);
		return invocation.proceed();
	}

	@Override
	public Object getProxy() {
		return getProxy(this.config.getTargetClass().getClassLoader());
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		return Proxy.newProxyInstance(classLoader, this.config.getTargetClass().getInterfaces(), this);
	}

}
