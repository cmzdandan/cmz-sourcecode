package com.cmz.spring.formework.aop.intercept;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午4:01:27
 * @description 方法拦截器
 */
public interface CmzMethodInterceptor {

	Object invoke(CmzMethodInvocation mi) throws Throwable;

}
