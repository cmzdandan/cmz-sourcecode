package com.cmz.netty.buffer;

import java.nio.ByteBuffer;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月17日 下午11:16:48
 * @description 缓冲区分片
 *              <p>
 *              调用 slice()方法可以创建一个子缓冲区
 *              <p>
 *              在 NIO 中，除了可以分配或者包装一个缓冲区对象外，还可以根据现有的缓冲区对象来创建一个子缓冲区
 *              <p>
 *              现有的缓冲区与创建的子缓冲区在底层数组层面上是数据共享的
 *              <p>
 *              子缓冲区相当于是现有缓冲区的一个视图窗口
 */
public class BufferSlice {

	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		// 缓冲区中的数据 0-9
		for (int i = 0; i < buffer.capacity(); ++i) {
			buffer.put((byte) i);
		}
		// 创建子缓冲区
		buffer.position(3);
		buffer.limit(7);
		ByteBuffer slice = buffer.slice();
		// 改变子缓冲区的内容
		for (int i = 0; i < slice.capacity(); ++i) {
			byte b = slice.get(i);
			b *= 10;
			slice.put(i, b);
		}
		buffer.position(0);
		buffer.limit(buffer.capacity());
		while (buffer.remaining() > 0) {
			System.out.println(buffer.get());
		}
	}

}
