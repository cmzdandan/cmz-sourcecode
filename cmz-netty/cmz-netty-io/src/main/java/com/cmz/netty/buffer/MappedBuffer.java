package com.cmz.netty.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月17日 下午11:42:32
 * @description 内存映射
 *              <p>
 * 				内存映射是一种读和写文件数据的方法，它可以比常规的基于流或者基于通道的 I/O 快的多
 *              <p>
 * 				只有文件中实际读取或者写入的部分才会映射到内存中
 */
public class MappedBuffer {
	
	private static final int start = 0;
	private static final int size = 1024;

	public static void main(String[] args) throws Exception {
		RandomAccessFile raf = new RandomAccessFile("D://test.txt", "rw");
		FileChannel fc = raf.getChannel();
		//把缓冲区跟文件系统进行一个映射关联
		//只要操作缓冲区里面的内容，文件内容也会跟着改变
		MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, start, size);
		buffer.put(0, (byte)97);
		buffer.put(1023, (byte)122);
		raf.close();
	}

}
