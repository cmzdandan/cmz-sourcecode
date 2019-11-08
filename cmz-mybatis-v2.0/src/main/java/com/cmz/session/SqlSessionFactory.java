package com.cmz.session;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午10:41:48
 * @description 会话工厂类，用于解析配置文件，产生SqlSession
 */
public class SqlSessionFactory {

	private Configuration configuration;
	
	/**
	 * 在构造方法中初始化Configuration对象
	 */
	public SqlSessionFactory() {
		configuration = new Configuration();
	}

	/**
	 * 获取DefaultSqlSession
	 * 
	 * @return
	 */
	public DefaultSqlSession openSqlSession() {
		return new DefaultSqlSession(configuration);
	}

}
