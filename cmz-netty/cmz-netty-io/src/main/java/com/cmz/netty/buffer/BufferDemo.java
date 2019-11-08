package com.cmz.netty.buffer;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月17日 下午10:53:34
 * @description Buffer的基本的原理
 *              <p>
 *              验证 position、limit 和 capacity 这几个值的变化过程
 */
public class BufferDemo {

	public static void main(String[] args) throws Exception {
		// 这用用的是文件 IO 处理
		FileInputStream fin = new FileInputStream("D://test.txt");
		// 创建文件的操作管道
		FileChannel fc = fin.getChannel();
		// 分配一个 10 个大小缓冲区，说白了就是分配一个 10 个大小的 byte 数组
		ByteBuffer buffer = ByteBuffer.allocate(10);
		output("初始化", buffer);
		// 先读一下
		fc.read(buffer);
		output("调用 read()", buffer);
		// 准备操作之前，先锁定操作范围
		buffer.flip();
		output("调用 flip()", buffer);
		// 判断有没有可读数据
		while (buffer.remaining() > 0) {
			byte b = buffer.get();
			System.out.print(((char) b));
		}
		output("调用 get()", buffer);
		// 可以理解为解锁
		buffer.clear();
		output("调用 clear()", buffer);
		// 最后把管道关闭
		fin.close();
	}

	// 把这个缓冲里面实时状态给打印出来
	private static void output(String step, ByteBuffer buffer) {
		System.out.println(step + " : ");
		// 容量，数组大小
		System.out.print("capacity: " + buffer.capacity() + ", ");
		// 当前操作数据所在的位置，也可以叫做游标
		System.out.print("position: " + buffer.position() + ", ");
		// 锁定值，flip，数据操作范围索引只能在 position - limit 之间
		System.out.println("limit: " + buffer.limit());
		System.out.println();
	}

}
