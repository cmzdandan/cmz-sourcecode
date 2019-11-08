package com.cmz.netty.channel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午1:18:11
 * @description channel示例(写入文件)
 */
public class FileOutputDemo {

	private static final byte message[] = { 83, 111, 109, 101, 32, 98, 121, 116, 101, 115, 46 };
	
	public static void main(String[] args) throws Exception {
		FileOutputStream outputStream = new FileOutputStream("D:\\test.txt");
		FileChannel channel = outputStream.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		for (byte b : message) {
			buffer.put(b);
		}
		buffer.flip();
		channel.write(buffer);
		outputStream.close();
	}

}
