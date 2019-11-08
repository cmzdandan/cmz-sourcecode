package com.cmz.executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cmz.session.Configuration;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 下午9:49:00
 * @description 封装JDBC Statement，用于操作数据库
 */
public class StatementHandler {

	private ResultSetHandler resultSetHandler = new ResultSetHandler();

	/**
	 * 执行查询
	 * 
	 * @param statement
	 * @param parameter
	 * @param pojo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T query(String statement, Object[] parameter, Class<?> pojo) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		Object result = null;
		try {
			conn = getConnection();
			preparedStatement = conn.prepareStatement(statement);
			ParameterHandler parameterHandler = new ParameterHandler(preparedStatement);
			parameterHandler.setParameters(parameter);
			preparedStatement.execute();
			try {
				result = resultSetHandler.handle(preparedStatement.getResultSet(), pojo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (T) result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 */
	private Connection getConnection() {
		String driver = Configuration.properties.getString("jdbc.driver");
		String url = Configuration.properties.getString("jdbc.url");
		String username = Configuration.properties.getString("jdbc.username");
		String password = Configuration.properties.getString("jdbc.password");
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
