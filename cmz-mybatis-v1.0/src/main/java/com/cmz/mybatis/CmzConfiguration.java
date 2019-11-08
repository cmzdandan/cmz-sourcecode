package com.cmz.mybatis;

import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午3:12:59
 * @description Configuration
 *              <p>
 *              1.获取代理对象； 2.存储了我们的接口方法（也就是statementId）和 SQL 语句的绑定关系。
 *              </p>
 */
public class CmzConfiguration {
	
	// 为了避免重复解析，定义一个静态的属性和静态方法，直接解析 sql.properties 文件里面的KV键值对
	public static final ResourceBundle sqlMappings;
	
	static {
		sqlMappings = ResourceBundle.getBundle("sql");
	}

	/**
	 * 通过JDK动态代理来创建代理对象
	 * <p>
	 * 三要素：1.类加载器；2.接口；3.InvocationHandler实现类
	 * 
	 * @param clazz
	 * @param sqlSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> clazz, CmzSqlSession sqlSession) {
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz },
				new CmzMapperProxy(sqlSession));
	}

}
