package com.cmz.spring.formework.aop;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 上午11:20:28
 * @description Aop代理接口，默认就用 JDK 动态代理
 */
public interface CmzAopProxy {

	Object getProxy();

	Object getProxy(ClassLoader classLoader);

}
