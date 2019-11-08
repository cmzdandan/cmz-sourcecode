package com.cmz.netty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月12日 下午9:49:30
 * @description NioServer
 */
public class NioServer {

	// 准备两个东西：轮询器、缓冲区
	// 轮询器：Selector	大堂经理
	private Selector selector;
	// 缓冲区：Buffer		等候区
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	private int port;
	
	public NioServer(int port) {
		try {
			this.port = port;
			// 大堂经理，开门并做准备工作
			ServerSocketChannel server = ServerSocketChannel.open();
			// 告诉客人地址
			server.bind(new InetSocketAddress(port));
			// Bio 升级版本 Nio，为了兼容Bio，Nio模型默认是采用阻塞式
			server.configureBlocking(false);
			// 大堂经理准备就绪，接客
			selector = Selector.open();
			// 宣布准备就绪
			server.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listen() {
		System.out.println("listen on " + this.port);
		try {
			// 轮询主线程
			while(true) {
				// 大堂经理开始叫号
				selector.select();
				// 发了很多号出去了，
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				// 不断的迭代，就叫轮询
				// 同步体现在这里，因为每次只能拿到一个key，每次只能处理一种状态
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					// 每个key代表一种状态
					process(key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 具体办业务的方法，柜员<p>
	 * 每次轮询就调用一次process方法，而每次调用，只能干一件事<p>
	 * 同一时间点，只能干一件事
	 * @param key
	 * @throws IOException
	 */
	private void process(SelectionKey key) throws IOException {
		// 对每种状态给一个响应
		if(key.isAcceptable()) {
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			key = channel.register(selector, SelectionKey.OP_READ);
		} else if(key.isReadable()) {
			// 从多路复用器中拿到客户端的引用
			SocketChannel channel = (SocketChannel) key.channel();
			int len = channel.read(buffer);
			if(len > 0) {
				buffer.flip();
				String content = new String(buffer.array(), 0, len);
				key = channel.register(selector, SelectionKey.OP_WRITE);
				// 还可以把content作为一个附件绑定到key上，一会儿可以写出去
				key.attach(content);
				System.out.println("收到信息：" + content);
			}
		} else if(key.isWritable()) {
			SocketChannel channel = (SocketChannel) key.channel();
			String content = (String) key.attachment();
			channel.write(ByteBuffer.wrap(("输出：" + content).getBytes()));
		}
	}

	public static void main(String[] args) {
		new NioServer(8080).listen();
	}
}
