package com.cmz.spring.formework.aop;

import com.cmz.spring.formework.aop.support.CmzAdvisedSupport;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 上午11:22:05
 * @description Cglib动态代理
 */
public class CmzCglibAopProxy implements CmzAopProxy {
	
	private CmzAdvisedSupport config;
	
	public CmzCglibAopProxy(CmzAdvisedSupport config) {
		this.config = config;
	}

	@Override
	public Object getProxy() {
		return null;
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		return null;
	}

}
