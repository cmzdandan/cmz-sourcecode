package com.cmz.orm.framework;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月14日 下午6:24:33
 * @description 排序(字段-升降)
 */
public class Order {

	// 升序还是降序
	private boolean ascending;

	// 哪个字段升序，哪个字段降序
	private String propertyName;

	public String toString() {
		return propertyName + ' ' + (ascending ? "asc" : "desc");
	}

	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}

	/**
	 * Ascending order
	 * 
	 * @param propertyName
	 * @return
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 * 
	 * @param propertyName
	 * @return
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

}
