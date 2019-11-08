package com.cmz.spring.formework.aop.aspect;

import java.lang.reflect.Method;

import com.cmz.spring.formework.aop.intercept.CmzMethodInterceptor;
import com.cmz.spring.formework.aop.intercept.CmzMethodInvocation;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午4:12:42
 * @description 异常通知
 */
public class CmzAfterThrowingAdvice extends CmzAbstractAspectJAdvice implements CmzAdvice, CmzMethodInterceptor {

	private String throwingName;
	private CmzMethodInvocation mi;

	public CmzAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
		super(aspectMethod, aspectTarget);
	}

	public void setThrowingName(String name) {
		this.throwingName = name;
	}

	@Override
	public Object invoke(CmzMethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		} catch (Exception e) {
			invokeAdviceMethod(mi, null, e.getCause());
			throw e;
		}
	}

}
