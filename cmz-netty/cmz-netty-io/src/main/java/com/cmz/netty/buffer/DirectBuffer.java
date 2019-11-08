package com.cmz.netty.buffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月17日 下午11:31:57
 * @description 直接缓冲区
 *              <p>
 * 				直接缓冲区是为加快 I/O 速度，使用一种特殊方式为其分配内存的缓冲区
 *              <p>
 * 				它会在每一次调用底层操作系统的本机 I/O
 *              操作之前(或之后)，尝试避免将缓冲区的内容拷贝到一个中间缓冲区中或者从一个中间缓冲区中拷贝数据
 *              <p>
 * 				要分配直接缓冲区，需要调用 allocateDirect()方法，而不是 allocate()方法，使用方式与普通缓冲区并无区别
 */
public class DirectBuffer {

	public static void main(String[] args) throws Exception {
		//首先我们从磁盘上读取刚才我们写出的文件内容
		FileInputStream fin = new FileInputStream("D://test.txt");
		FileChannel fcin = fin.getChannel();
		//把刚刚读取的内容写入到一个新的文件中
		String outfile = String.format("D://testcopy.txt");
		FileOutputStream fout = new FileOutputStream(outfile);
		FileChannel fcout = fout.getChannel();
		// 使用 allocateDirect，而不是 allocate
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		while(true) {
			buffer.clear();
			int read = fcin.read(buffer);
			if(read == -1) {
				break;
			}
			buffer.flip();
			fcout.write(buffer);
		}
		fout.close();
		fin.close();
	}

}
