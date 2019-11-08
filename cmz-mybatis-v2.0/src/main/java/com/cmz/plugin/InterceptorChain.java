package com.cmz.plugin;

import java.util.ArrayList;
import java.util.List;


/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午9:53:52
 * @description 拦截器链，存放所有拦截器，和对代理对象进行循环代理
 */
public class InterceptorChain {

	private final List<Interceptor> interceptors = new ArrayList<>();
	
	/**
	 * 向拦截器链中添加一个拦截器
	 * @param interceptor
	 */
	public void addInterceptor(Interceptor interceptor){
		interceptors.add(interceptor);
	}
	
	/**
	 * 对被拦截对象进行层层代理
	 * @param target
	 * @return
	 */
	public Object pluginAll(Object target) {
		for(Interceptor interceptor : interceptors) {
			target = interceptor.plugin(target);
		}
		return target;
	}
	
	/**
	 * 是否有插件
	 * @return
	 */
	public boolean hasPlugin() {
		return !(interceptors.size() == 0);
	}
	
}
