package com.cmz.binding;

import java.lang.reflect.Proxy;

import com.cmz.session.DefaultSqlSession;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午10:26:58
 * @description 用于产生MapperProxy代理类
 */
public class MapperProxyFactory<T> {

	private Class<T> mapperInterface;
	
	private Class<?> object;
	
	public MapperProxyFactory(Class<T> mapperInterface, Class<?> object) {
		this.mapperInterface = mapperInterface;
		this.object = object;
	}
	
	@SuppressWarnings("unchecked")
	public T newInstance(DefaultSqlSession sqlSession) {
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, new MapperProxy(sqlSession, object));
	}
	
}
