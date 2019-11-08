package com.cmz.executor;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午9:39:49
 * @description 操作数据库的执行器接口
 */
public interface Executor {

	/**
	 * 定义查询方法
	 * 
	 * @param statement
	 *            SQL语句
	 * @param parameter
	 *            参数
	 * @param pojo
	 *            实体类对象
	 * @return
	 */
	<T> T query(String statement, Object[] parameter, Class<T> pojo);

}
