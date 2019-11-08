package com.cmz.netty.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月29日 下午4:30:00
 * @description response响应
 */
public class CmzResponse {

	// SocketChannel的封装
	private ChannelHandlerContext context;

	private HttpRequest request;

	public CmzResponse(ChannelHandlerContext ctx, HttpRequest req) {
		this.context = ctx;
		this.request = req;
	}

	public void write(String out) throws Exception {
		try {
			if (out == null || out.length() == 0) {
				return;
			}
			// 设置 http协议及请求头信息
			FullHttpResponse response = new DefaultFullHttpResponse(
					// 设置http版本为1.1
					HttpVersion.HTTP_1_1,
					// 设置响应状态码
					HttpResponseStatus.OK,
					// 将输出值写出 编码为UTF-8
					Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
			response.headers().set("Content-Type", "text/html;");
			context.write(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.flush();
			context.close();
		}
	}

}
