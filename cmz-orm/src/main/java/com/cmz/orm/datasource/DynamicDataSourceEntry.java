package com.cmz.orm.datasource;

import org.aspectj.lang.JoinPoint;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月16日 上午10:15:31
 * @description 动态数据源实体类
 */
public class DynamicDataSourceEntry {

	// 默认数据源
	public final static String DEFAULT_SOURCE = null;

	private final static ThreadLocal<String> local = new ThreadLocal<String>();

	/**
	 * 清空数据源
	 */
	public void clear() {
		local.remove();
	}

	/**
	 * 获取当前正在使用的数据源名字
	 *
	 * @return String
	 */
	public String get() {
		return local.get();
	}

	/**
	 * 还原指定切面的数据源
	 *
	 * @param joinPoint
	 */
	public void restore(JoinPoint join) {
		local.set(DEFAULT_SOURCE);
	}

	/**
	 * 还原当前切面的数据源
	 */
	public void restore() {
		local.set(DEFAULT_SOURCE);
	}

	/**
	 * 设置已知名字的数据源
	 *
	 * @param dataSource
	 */
	public void set(String source) {
		local.set(source);
	}

	/**
	 * 根据年份动态设置数据源
	 * 
	 * @param year
	 */
	public void set(int year) {
		local.set("DB_" + year);
	}

}
