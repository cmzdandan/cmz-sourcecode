package com.cmz.netty.io.bio;
/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月12日 下午9:32:56
 * @description BioServer
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

	//服务端网络IO模型的封装对象
	ServerSocket server;
	
	// 服务器
	public BioServer(int port) {
		try {
			// tomcat 默认端口8080
			// 只要是Java写的，都是这么玩，底层都是new一个ServerSocket
			server = new ServerSocket(port);
			System.out.println("BIO服务已经启动，监听端口是：" + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listen() throws IOException {
		while(true) {
			// 等待客户端连接，阻塞方法
			Socket socket = server.accept();
			// 对方发送数据过来了，把发送过来的东西当作一个输入流
			InputStream inputStream = socket.getInputStream();
			// 构造缓存区
			byte[] buffer = new byte[1024];
			// 我们要把它读入到内存中
			int len = inputStream.read(buffer);
			if(len > 0) {
				String message = new String(buffer, 0, len);
				System.out.println("收到数据：" + message);
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new BioServer(8080).listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
