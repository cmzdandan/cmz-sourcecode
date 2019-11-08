package com.cmz.executor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 下午9:50:23
 * @description 结果集处理器
 */
public class ResultSetHandler {

	/**
	 * 处理结果集，返回映射后的实体对象
	 * 
	 * @param resultSet
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T handle(ResultSet resultSet, Class<?> type) {
		// 直接调用Class的方法产生一个实例
		Object pojo = null;
		try {
			pojo = type.newInstance();
			if (resultSet.next()) {
				for (Field field : pojo.getClass().getDeclaredFields()) {
					setValue(field, pojo, resultSet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) pojo;
	}

	/**
	 * 通过反射给属性赋值
	 * <p>
	 * 根据pojo拿到其Class对象，再从Class对象中拿到指定名称的Method对象，该名称是set和属性名组合而成的，其实就是pojo的set方法
	 * <p>
	 * 拿到Method方法后，调用invoke()方法，传入
	 * pojo，即执行pojo对象的当前方法，该方法是一个赋值方法，即一个set方法，后面一个参数即要赋的值
	 * 
	 * @param field
	 * @param pojo
	 * @param resultSet
	 */
	private void setValue(Field field, Object pojo, ResultSet resultSet) {
		try {
			String fieldName = field.getName();
			if("serialVersionUID".equals(fieldName)) {
				// 忽略默认序列号属性
				return;
			}
			String methodName = "set" + firstWordCapital(fieldName);
			Method setMethod = pojo.getClass().getMethod(methodName, field.getType());
			setMethod.invoke(pojo, getFieldValue(resultSet, field));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取属性值
	 * <p>
	 * 根据 field对象，获取属性名，根据属性名驼峰转下划线得到数据库字段名，再做类型转换，最后从结果集取对应的值
	 * 
	 * @param resultSet
	 * @param field
	 * @return
	 * @throws SQLException
	 */
	private Object getFieldValue(ResultSet resultSet, Field field) throws SQLException {
		Class<?> type = field.getType();
		// 驼峰转下划线，得到数据库字段
		String fieldName = humpToUnderline(field.getName());
		if (type == Integer.class) {
			return resultSet.getInt(fieldName);
		} else if (String.class == type) {
			return resultSet.getString(fieldName);
		} else if (Long.class == type) {
			return resultSet.getLong(fieldName);
		} else if (Boolean.class == type) {
			return resultSet.getBoolean(fieldName);
		} else if (Double.class == type) {
			return resultSet.getDouble(fieldName);
		} else {
			return resultSet.getString(fieldName);
		}
	}

	/**
	 * 驼峰转下划线
	 * 
	 * @param string
	 * @return
	 */
	private String humpToUnderline(String string) {
		StringBuilder sb = new StringBuilder(string);
		int temp = 0;
		if (!string.contains("_")) {
			for (int i = 0; i < string.length(); i++) {
				if (Character.isUpperCase(string.charAt(i))) {
					sb.insert(i + temp, "_");
					temp += 1;
				}
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * 首字母变大写
	 * 
	 * @param word
	 * @return
	 */
	private String firstWordCapital(String word) {
		String first = word.substring(0, 1);
		String tail = word.substring(1);
		return first.toUpperCase() + tail;
	}

}
