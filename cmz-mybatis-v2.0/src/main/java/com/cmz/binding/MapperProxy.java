package com.cmz.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.cmz.session.DefaultSqlSession;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午10:29:44
 * @description MapperProxy代理类，用于代理Mapper接口
 */
public class MapperProxy implements InvocationHandler {
	
	private DefaultSqlSession sqlSession;
    private Class<?> object;
    
    public MapperProxy(DefaultSqlSession sqlSession, Class<?> object) {
        this.sqlSession = sqlSession;
        this.object = object;
    }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String mapperInterface = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String statementId = mapperInterface + "." + methodName;
        // 如果根据接口类型+方法名能找到映射的SQL，则执行SQL
        if (sqlSession.getConfiguration().hasStatement(statementId)) {
            return sqlSession.selectOne(statementId, args, object);
        }
        // 否则直接执行被代理对象的原方法
		return method.invoke(proxy, args);
	}

}
