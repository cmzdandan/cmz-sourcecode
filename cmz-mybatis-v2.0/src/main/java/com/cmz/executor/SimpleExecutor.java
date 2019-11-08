package com.cmz.executor;
/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午9:45:37
 * @description 简单执行器(执行器的默认实现)
 */
public class SimpleExecutor implements Executor {

	@Override
	public <T> T query(String statement, Object[] parameter, Class<T> pojo) {
		StatementHandler statementHandler = new StatementHandler();
		return statementHandler.query(statement, parameter, pojo);
	}

}
