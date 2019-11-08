package com.cmz.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.cmz.annotation.Intercepts;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 下午10:35:38
 * @description 代理类，用于代理被拦截对象,同时提供了创建代理类的方法
 */
public class Plugin implements InvocationHandler {

	// 被代理的对象
	private Object target;

	// 拦截器接口(插件)
	private Interceptor interceptor;

	public Plugin(Object target, Interceptor interceptor) {
		this.target = target;
		this.interceptor = interceptor;
	}
	
	public static Object wrap(Object obj, Interceptor interceptor) {
		Class<?> clazz = obj.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new Plugin(obj, interceptor));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 自定义的插件上有@Intercepts注解，指定了拦截的方法
		if(interceptor.getClass().isAnnotationPresent(Intercepts.class)) {
			// 如果是被拦截的方法，则进入自定义拦截器的逻辑
			if(method.getName().equals(interceptor.getClass().getAnnotation(Intercepts.class).value())) {
				return interceptor.intercept(new Invocation(target, method, args));
			}
		}
		// 非被拦截方法，执行原逻辑
		return method.invoke(proxy, args);
	}

}
