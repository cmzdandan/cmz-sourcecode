package com.cmz.netty.rpc.consumer;

import com.cmz.netty.rpc.api.ArithmeticService;
import com.cmz.netty.rpc.api.HelloService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午10:16:45
 * @description 客户端调用代码
 */
public class RpcConsumer {

	public static void main(String[] args) {
		HelloService helloService = RpcProxy.create(HelloService.class);
		String result = helloService.hello("Cmz");
		System.out.println(result);

		ArithmeticService arithmeticService = RpcProxy.create(ArithmeticService.class);
		System.out.println("8 + 2 = " + arithmeticService.add(8, 2));
		System.out.println("8 - 2 = " + arithmeticService.sub(8, 2));
		System.out.println("8 * 2 = " + arithmeticService.mult(8, 2));
		System.out.println("8 / 2 = " + arithmeticService.div(8, 2));
	}

}
