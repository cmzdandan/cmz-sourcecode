package com.cmz.netty.rpc.api;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午8:09:56
 * @description 完成模拟业务加、减、乘、除运算
 */
public interface ArithmeticService {

	/** 加 */
	public int add(int a, int b);

	/** 减 */
	public int sub(int a, int b);

	/** 乘 */
	public int mult(int a, int b);

	/** 除 */
	public int div(int a, int b);

}
