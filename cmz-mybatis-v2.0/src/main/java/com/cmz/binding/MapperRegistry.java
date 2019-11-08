package com.cmz.binding;

import java.util.HashMap;
import java.util.Map;

import com.cmz.session.DefaultSqlSession;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午10:24:36
 * @description 维护接口和工厂类的关系，用于获取MapperProxy代理对象
 *              <p>
 *              工厂类指定了POJO类型，用于处理结果集返回
 *              </p>
 */
public class MapperRegistry {

	// 接口和工厂类映射关系
	private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

	/**
	 * 在Configuration中解析接口上的注解时，存入接口和工厂类的映射关系
	 * <p>
	 * 此处传入pojo类型，是为了最终处理结果集的时候将结果转换为POJO类型
	 * 
	 * @param clazz
	 * @param pojo
	 */
	public <T> void addMapper(Class<?> clazz, Class<T> pojo) {
		knownMappers.put(clazz, new MapperProxyFactory<>(clazz, pojo));
	}

	/**
	 * 创建一个代理对象
	 * 
	 * @param clazz
	 * @param sqlSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {
		MapperProxyFactory<?> proxyFactory = knownMappers.get(clazz);
		if (proxyFactory == null) {
			throw new RuntimeException("Type: " + clazz + " can not find");
		}
		return (T) proxyFactory.newInstance(sqlSession);
	}

}
