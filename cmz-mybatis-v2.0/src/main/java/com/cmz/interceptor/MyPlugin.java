package com.cmz.interceptor;

import java.util.Arrays;

import com.cmz.annotation.Intercepts;
import com.cmz.plugin.Interceptor;
import com.cmz.plugin.Invocation;
import com.cmz.plugin.Plugin;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午11:57:24
 * @description 自定义插件
 */
@Intercepts("query")
public class MyPlugin implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		String statement = (String) invocation.getArgs()[0];
		Object[] parameter = (Object[]) invocation.getArgs()[1];
		Class<?> pojo = (Class<?>) invocation.getArgs()[2];
		System.out.println("插件输出：SQL：[" + statement + "]");
		System.out.println("插件输出：Parameters：" + Arrays.toString(parameter));
		System.out.println("插件输出：Pojo：" + pojo);
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

}
