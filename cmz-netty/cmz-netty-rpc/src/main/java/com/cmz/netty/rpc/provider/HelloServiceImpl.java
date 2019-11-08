package com.cmz.netty.rpc.provider;

import com.cmz.netty.rpc.api.HelloService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午8:17:56
 * @description 服务提供者实现类
 */
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "Hello " + name + "!";
	}

}
