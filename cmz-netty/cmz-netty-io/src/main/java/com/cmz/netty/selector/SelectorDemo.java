package com.cmz.netty.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
 * @create 2019年6月18日 下午1:25:21
 * @description SelectorDemo
 *              <p>
 *              使用 NIO 中非阻塞 I/O 编写服务器处理程序，大体上可以分为下面三个步骤:
 *              <p>
 *              A. 向 Selector 对象注册感兴趣的事件；
 *              <p>
 *              B. 从 Selector 中获取感兴趣的事件；
 *              <p>
 *              C. 根据不同的事件进行相应的处理。
 */
public class SelectorDemo {

	private int port = 8080;

	private Selector selector;
	
	private ByteBuffer buffer;

	public static void main(String[] args) {

	}

	/**
	 * 注册事件
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private Selector getSelector() throws IOException {
		// 创建 Selector 对象
		Selector selector = Selector.open();

		// 创建可选择通道，并配置为非阻塞模式
		ServerSocketChannel server = ServerSocketChannel.open();
		server.configureBlocking(false);

		// 绑定通道到指定端口
		ServerSocket socket = server.socket();
		InetSocketAddress address = new InetSocketAddress(port);
		socket.bind(address);

		// 向 Selector 中注册感兴趣的事件
		// 指定我们想要监听 accept 事件，也就是新的连接发生时所产生的事件
		// 对于 ServerSocketChannel 通道来说，我们唯一可以指定的参数就是 OP_ACCEPT
		server.register(selector, SelectionKey.OP_ACCEPT);
		return selector;
	}

	/**
	 * 开始监听
	 * <p>
	 * 在非阻塞 I/O 中，内部循环模式基本都是遵循这种方式。
	 * <p>
	 * 首先调用 select()方法，该方法会阻塞，直到至少有一个事件发生
	 * <p>
	 * 然后再使用 selectedKeys()方法获取发生事件的 SelectionKey
	 * <p>
	 * 再使用迭代器进行循环。
	 */
	public void listen() {
		System.out.println("listen on " + port);
		try {
			while (true) {
				// 该调用会阻塞，直到至少有一个事件发生
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					process(key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 最后一步就是根据不同的事件，编写相应的处理代码
	 * @param key
	 * @throws Exception 
	 */
	private void process(SelectionKey key) throws Exception {
		// 接收请求
		if(key.isAcceptable()) {
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		}
		// 读信息
		else if (key.isReadable()) {
			SocketChannel channel = (SocketChannel) key.channel();
			int len = channel.read(buffer);
			if(len > 0) {
				buffer.flip();
				String content = new String(buffer.array(), 0, len);
				SelectionKey skey = channel.register(selector, SelectionKey.OP_WRITE);
				skey.attach(content);
			}
		}
		// 写事件
		else if(key.isWritable()) {
			SocketChannel channel = (SocketChannel) key.channel();
			String content = (String) key.attachment();
			ByteBuffer block = ByteBuffer.wrap(("输出内容：" + content).getBytes());
			if(block != null) {
				channel.write(block);
			} else {
				channel.close();
			}
		}
	}

}
