package com.cmz.executor;

import java.sql.PreparedStatement;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 下午9:53:10
 * @description 参数处理器
 */
public class ParameterHandler {

	private PreparedStatement psmt;

	public ParameterHandler(PreparedStatement statement) {
		this.psmt = statement;
	}

	/**
	 * 从方法中获取参数，遍历设置SQL中的？占位符
	 * 
	 * @param parameters
	 */
	public void setParameters(Object[] parameters) {
		try {
			// PreparedStatement的序号是从1开始的
			for (int i = 0; i < parameters.length; i++) {
				int k = i + 1;
				if (parameters[i] instanceof Integer) {
					psmt.setInt(k, (Integer) parameters[i]);
				} else if (parameters[i] instanceof Long) {
                    psmt.setLong(k, (Long) parameters[i]);
                } else if (parameters[i] instanceof String) {
                    psmt.setString(k , String.valueOf(parameters[i]));
                } else if (parameters[i] instanceof Boolean) {
                    psmt.setBoolean(k, (Boolean) parameters[i]);
                } else {
                    psmt.setString(k, String.valueOf(parameters[i]));
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
