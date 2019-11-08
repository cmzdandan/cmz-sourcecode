package com.cmz.netty.channel;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午1:10:42
 * @description channel示例(读入文件)
 */
public class FileInputDemo {

	public static void main(String[] args) throws Exception {
		FileInputStream inputStream = new FileInputStream("D:\\test.txt");
		FileChannel channel = inputStream.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(10);
		// 将数据读到缓冲区
		channel.read(buffer);
		// 刷新缓冲区
		buffer.flip();
		// 取缓冲区中的内容
		while (buffer.hasRemaining()) {
			System.out.println((char) buffer.get());
		}
		inputStream.close();
	}

}
