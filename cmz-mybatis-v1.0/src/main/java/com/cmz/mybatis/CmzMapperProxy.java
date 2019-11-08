package com.cmz.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午3:29:43
 * @description MapperProxy
 */
public class CmzMapperProxy implements InvocationHandler {
	
	// 因为在 invoke() 方法里面需要调用SqlSession的 selectOne()方法，所以，这里必须持有SqlSession对象
	private CmzSqlSession sqlSession;
	
	// 通过构造器注入
	public CmzMapperProxy(CmzSqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 拿到接口名称(全类名)
		String mapperInterface = method.getDeclaringClass().getName();
		// 拿到方法名称
		String methodName = method.getName();
		// statementId 其实就是接口的全路径+方法名，中间加一个英文的点
		String statementId = mapperInterface + "." + methodName;
		return sqlSession.selectOne(statementId, args[0]);
	}

}
