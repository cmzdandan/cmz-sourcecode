package com.cmz.spring.formework.aop.aspect;

import java.lang.reflect.Method;

import com.cmz.spring.formework.aop.intercept.CmzMethodInterceptor;
import com.cmz.spring.formework.aop.intercept.CmzMethodInvocation;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午4:11:23
 * @description 后置通知
 */
public class CmzAfterReturningAdvice extends CmzAbstractAspectJAdvice implements CmzAdvice, CmzMethodInterceptor {

	private CmzJoinPoint joinPoint;

	public CmzAfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
		super(aspectMethod, aspectTarget);
	}

	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		invokeAdviceMethod(joinPoint, returnValue, null);
	}

	@Override
	public Object invoke(CmzMethodInvocation mi) throws Throwable {
		Object retVal = mi.proceed();
		this.joinPoint = mi;
		this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
		return retVal;
	}

}
