package com.cmz.plugin;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午9:55:59
 * @description 拦截器接口，所有自定义拦截器必须实现此接口
 */
public interface Interceptor {

	/**
	 * 插件的核心逻辑实现
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	Object intercept(Invocation invocation) throws Throwable;

	/**
	 * 对被拦截的对象进行代理
	 * 
	 * @param target
	 * @return
	 */
	Object plugin(Object target);

}
