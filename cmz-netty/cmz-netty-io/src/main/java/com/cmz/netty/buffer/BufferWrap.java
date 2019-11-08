package com.cmz.netty.buffer;

import java.nio.ByteBuffer;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月17日 下午11:01:54
 * @description 缓冲区的分配可以直接分配，也可以包装一个数组
 *              <p>
 *              在创建一个缓冲区对象时，会调用静态方法 allocate()来指定缓冲区的容量
 *              <p>
 *              其实调用allocate()相当于创建了一个指定大小的数组，并把它包装为缓冲区对象
 *              <p>
 *              我们也可以直接将一个现有的数组，包装为缓冲区对象
 */
public class BufferWrap {

	public void myMethod() {
		// 分配指定大小的缓冲区
		ByteBuffer buffer1 = ByteBuffer.allocate(10);
		System.out.println(buffer1);
		
		// 包装一个现有的数组
		byte array[] = new byte[10];
		ByteBuffer buffer2 = ByteBuffer.wrap(array);
		System.out.println(buffer2);
	}

}
