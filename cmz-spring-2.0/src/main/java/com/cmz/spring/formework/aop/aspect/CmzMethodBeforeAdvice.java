package com.cmz.spring.formework.aop.aspect;

import java.lang.reflect.Method;

import com.cmz.spring.formework.aop.intercept.CmzMethodInterceptor;
import com.cmz.spring.formework.aop.intercept.CmzMethodInvocation;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午4:00:32
 * @description 前置通知
 */
public class CmzMethodBeforeAdvice extends CmzAbstractAspectJAdvice implements CmzAdvice, CmzMethodInterceptor {

	private CmzJoinPoint joinPoint;

	public CmzMethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
		super(aspectMethod, aspectTarget);
	}

	public void before(Method method, Object[] args, Object target) throws Throwable {
		invokeAdviceMethod(this.joinPoint, null, null);
	}

	@Override
	public Object invoke(CmzMethodInvocation mi) throws Throwable {
		this.joinPoint = mi;
		this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
		return mi.proceed();
	}

}
