package com.cmz.netty.io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月12日 下午9:32:42
 * @description BioClient
 */
public class BioClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// 要和谁在哪个端口进行通信，IP、Port(这个表示服务器的IP和Port)
		Socket client = new Socket("localhost", 8080);
		// 输出 write，不管是服务端还是客户端，都有可能write和read
		OutputStream outputStream = client.getOutputStream();
		// 随机字符串作为数据
		String data = UUID.randomUUID().toString();
		System.out.println("准备发送的数据：" + data);
		// 将数据变为二进制
		byte[] byteData = data.getBytes();
		outputStream.write(byteData);
		client.close();
	}

}
