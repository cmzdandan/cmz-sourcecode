package com.cmz.netty.buffer;

import java.nio.ByteBuffer;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月17日 下午11:22:52
 * @description 只读缓冲区
 *              <p>
 * 				可以读取它们，但是不能向它们写入数据。可以通过调用缓冲区的
 *              asReadOnlyBuffer()方法，将任何常规缓冲区转换为只读缓冲区
 *              <p>
 * 				这个方法返回一个与原缓冲区完全相同的缓冲区，并与原缓冲区共享数据，只不过它是只读的
 *              <p>
 * 				如果原缓冲区的内容发生了变化，只读缓冲区的内容也随之发生变化
 */
public class ReadOnlyBuffer {

	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		// 缓冲区中的数据 0-9
		for (int i = 0; i < buffer.capacity(); i++) {
			buffer.put((byte) i);
		}
		// 创建只读缓冲区
		ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
		// 改变原缓冲区的内容
		for (int i = 0; i < buffer.capacity(); i++) {
			byte b = buffer.get(i);
			b *= 10;
			buffer.put(i, b);
		}
//		readOnlyBuffer.position(0);
//		readOnlyBuffer.limit(buffer.capacity());
		readOnlyBuffer.flip();// 等效于上面两句
		while(readOnlyBuffer.hasRemaining()) {
			System.out.println(readOnlyBuffer.get());
		}
	}

}
