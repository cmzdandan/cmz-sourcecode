package com.cmz.netty.tomcat;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.cmz.netty.tomcat.handler.CmzTomcatHandler;
import com.cmz.netty.tomcat.http.CmzServlet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午3:53:43
 * @description tomcat启动入口
 */
public class CmzTomcat {

	// 打开Tomcat源码，全局搜索ServerSocket

	private int port = 8080;

	private Map<String, CmzServlet> servletMapping = new HashMap<>();

	private Properties webxml = new Properties();

	private void init() {
		// 加载web.xml文件,同时初始化 ServletMapping对象
		try {
			String WEB_INF = this.getClass().getResource("/").getPath();
			FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
			webxml.load(fis);
			for (Object k : webxml.keySet()) {
				String key = k.toString();
				if (key.endsWith(".url")) {
					String servletName = key.replaceAll("\\.url$", "");
					String url = webxml.getProperty(key);
					String className = webxml.getProperty(servletName + ".className");
					CmzServlet servlet = (CmzServlet) Class.forName(className).newInstance();
					servletMapping.put(url, servlet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		init();
		// Netty封装了NIO，Reactor模型，Boss，worker
		// Boss线程
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		// Worker线程
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			// Netty服务  ServetBootstrap   ServerSocketChannel
			ServerBootstrap server = new ServerBootstrap();
			// 链路式编程
			server.group(bossGroup, workerGroup)
				// 主线程处理类,看到这样的写法，底层就是用反射
				.channel(NioServerSocketChannel.class)
				// 子线程处理类 , Handler
				.childHandler(new ChannelInitializer<SocketChannel>() {
					// 客户端初始化处理
					@Override
					protected void initChannel(SocketChannel client) throws Exception {
						// 无锁化串行编程
						//Netty对HTTP协议的封装，顺序有要求
						// HttpResponseEncoder 编码器
						client.pipeline().addLast(new HttpResponseEncoder());
						// HttpRequestDecoder 解码器
						client.pipeline().addLast(new HttpRequestDecoder());
						// 业务逻辑处理
						client.pipeline().addLast(new CmzTomcatHandler(servletMapping));
					}
					
				})
				// 针对主线程的配置 分配线程最大数量 128
				.option(ChannelOption.SO_BACKLOG, 128)
				// 针对子线程的配置 保持长连接
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			// 启动服务器
			ChannelFuture future = server.bind(port).sync();
			System.out.println("Cmz tomcat start. Listen on port " + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		new  CmzTomcat().start();
	}

}
