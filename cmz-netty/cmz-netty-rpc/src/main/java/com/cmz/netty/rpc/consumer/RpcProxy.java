package com.cmz.netty.rpc.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.cmz.netty.rpc.protocol.InvokerProtocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午9:28:07
 * @description 远程方法调用的代理类，即由 Netty 发送网络请求
 */
public class RpcProxy {

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<?> clazz) {
		// clazz 传进来本来就是一个 interface
		MethodProxy proxy = new MethodProxy(clazz);
		Class<?>[] interfaces = clazz.isInterface() ? new Class[] { clazz } : clazz.getInterfaces();
		T instance = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, proxy);
		return instance;
	}

	private static class MethodProxy implements InvocationHandler {
		private Class<?> clazz;

		public MethodProxy(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// 如果传进来是一个已实现的具体类（本次演示略过此逻辑)
			if (Object.class.equals(method.getDeclaringClass())) {
				try {
					return method.invoke(this, args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 如果传进来的是一个接口（核心)
				return rpcInvoke(proxy, method, args);
			}
			return null;
		}

		/**
		 * 实现接口的核心方法
		 * 
		 * @param proxy
		 * @param method
		 * @param args
		 * @return
		 */
		private Object rpcInvoke(Object proxy, Method method, Object[] args) {
			// 传输协议封装
			InvokerProtocol msg = new InvokerProtocol();
			msg.setClassName(this.clazz.getName());
			msg.setMethodName(method.getName());
			msg.setParamTypes(method.getParameterTypes());
			msg.setParamValues(args);
			final RpcProxyHandler consumerHandler = new RpcProxyHandler();
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap client = new Bootstrap();
				client.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {

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
								pipeline.addLast("handler", consumerHandler);
							}
						});
				ChannelFuture future = client.connect("192.168.0.100", 8080).sync();
				future.channel().writeAndFlush(msg).sync();
				future.channel().close().sync();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

}
