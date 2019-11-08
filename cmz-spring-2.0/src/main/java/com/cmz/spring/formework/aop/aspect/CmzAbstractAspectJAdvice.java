package com.cmz.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午3:53:38
 * @description 切面通知抽象类
 */
public abstract class CmzAbstractAspectJAdvice implements CmzAdvice {

	private Method aspectMethod;
	private Object aspectTarget;

	public CmzAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
		this.aspectMethod = aspectMethod;
		this.aspectTarget = aspectTarget;
	}

	protected Object invokeAdviceMethod(CmzJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {
		Class<?>[] paramsTypes = this.aspectMethod.getParameterTypes();
		if (null == paramsTypes || paramsTypes.length == 0) {
			return this.aspectMethod.invoke(aspectTarget);
		}
		Object[] args = new Object[paramsTypes.length];
		for (int i = 0; i < paramsTypes.length; i++) {
			if (paramsTypes[i] == CmzJoinPoint.class) {
				args[i] = joinPoint;
			} else if (paramsTypes[i] == Throwable.class) {
				args[i] = ex;
			} else if (paramsTypes[i] == Object.class) {
				args[i] = returnValue;
			}
		}
		return this.aspectMethod.invoke(aspectTarget, args);
	}

}
