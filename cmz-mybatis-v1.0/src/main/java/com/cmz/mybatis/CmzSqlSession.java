package com.cmz.mybatis;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午3:12:07
 * @description SqlSession
 */
public class CmzSqlSession {

	private CmzConfiguration configuration;

	private CmzExecutor executor;
	
	/**
	 * 防止出现直接使用空参构造函数而出现空指针问题，我们这里强制构造注入 Configuration对象和 Executor对象
	 * @param configuration
	 * @param executor
	 */
	public CmzSqlSession(CmzConfiguration configuration, CmzExecutor executor) {
		this.configuration = configuration;
		this.executor = executor;
	}

	/**
	 * 对外提供API，用来操作数据库，实际上是通过Executor来执行具体的Sql语句
	 * 
	 * @param statementId
	 * @param parameter
	 * @return
	 */
	public <T> T selectOne(String statementId, Object parameter) {
		// 从 Configuration 中获取解析好了的sql语句
		String sql = CmzConfiguration.sqlMappings.getString(statementId);
		// 如果 SQL语句拿不到，说明不存在映射关系[或者不是接口中定义的操作数据的方法，比如 toString()]
		if(null != sql && !"".equals(sql)) {
			return executor.query(sql, parameter);
		}
		return null;
	}

	/**
	 * 根据接口类型获取到代理对象，实际上是通过Configuration来获取的代理对象
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> T getMapper(Class<T> clazz) {
		return configuration.getMapper(clazz, this);
	}

}
