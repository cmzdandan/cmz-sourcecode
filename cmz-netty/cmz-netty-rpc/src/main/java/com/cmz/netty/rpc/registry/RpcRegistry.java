package com.cmz.netty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午8:22:12
 * @description 主要实现服务注册和服务调用的功能
 *              <p>
 *              Registry 注册中心主要功能就是负责将所有 Provider 的服务名称和服务引用地址注册到一个容器中，并对外发布。
 *              <p>
 *              Registry 应该要启动一个对外的服务，很显然应该作为服务端，并提供一个对外可以访问的端口 。
 */
public class RpcRegistry {

	private int port;

	public RpcRegistry(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		new RpcRegistry(8080).start();
	}

	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							// 自定义协议解码器
							/**
							 * 入参有 5 个，分别解释如下
							 * <p>
							 * maxFrameLength：框架的最大长度。如果帧的长度大于此值，则将抛出 TooLongFrameException。
							 * <p>
							 * lengthFieldOffset：长度字段的偏移量：即对应的长度字段在整个消息数据中得位置
							 * <p>
							 * lengthFieldLength：长度字段的长度。如：长度字段是 int 型表示，那么这个值就是 4（long 型就是 8）
							 * <p>
							 * lengthAdjustment：要添加到长度字段值的补偿值
							 * <p>
							 * initialBytesToStrip：从解码帧中去除的第一个字节数
							 */
							pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							// 自定义协议编码器
							pipeline.addLast(new LengthFieldPrepender(4));
							// 对象参数类型编码器
							pipeline.addLast("encoder", new ObjectEncoder());
							// 对象参数类型解码器
							pipeline.addLast("decoder",
									new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
							pipeline.addLast(new RpcRegistryHandler());
						}

					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = server.bind(port).sync();
			System.out.println("Cmz RPC registry start listen at " + port);
			future.channel().close().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
