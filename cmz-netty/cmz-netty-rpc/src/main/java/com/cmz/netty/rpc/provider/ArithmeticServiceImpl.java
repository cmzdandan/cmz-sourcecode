package com.cmz.netty.rpc.provider;

import com.cmz.netty.rpc.api.ArithmeticService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午8:19:01
 * @description 实现加减乘除运算逻辑
 */
public class ArithmeticServiceImpl implements ArithmeticService {

	@Override
	public int add(int a, int b) {
		return a + b;
	}

	@Override
	public int sub(int a, int b) {
		return a - b;
	}

	@Override
	public int mult(int a, int b) {
		return a * b;
	}

	@Override
	public int div(int a, int b) {
		return a / b;
	}

}
