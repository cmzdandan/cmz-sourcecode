package com.cmz.netty.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午9:58:09
 * @description 接收网络调用的返回值
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

	private Object response;

	public Object getResponse() {
		return response;
	}

	/**
	 * 接收网络调用的返回值
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		response = msg;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("client exception is general");
	}

}
