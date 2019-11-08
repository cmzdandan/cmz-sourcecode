package com.cmz.netty.tomcat.handler;

import java.util.Map;

import com.cmz.netty.tomcat.http.CmzRequest;
import com.cmz.netty.tomcat.http.CmzResponse;
import com.cmz.netty.tomcat.http.CmzServlet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月29日 下午5:04:24
 * @description 业务请求处理器
 */
public class CmzTomcatHandler extends ChannelInboundHandlerAdapter {
	
	private Map<String, CmzServlet> servletMapping;
	
	public CmzTomcatHandler(Map<String, CmzServlet> servletMapping) {
		this.servletMapping = servletMapping;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			// 转交给我们自己的request实现
			CmzRequest request = new CmzRequest(ctx, req);
			// 转交给我们自己的response实现
			CmzResponse response = new CmzResponse(ctx, req);
			// 实际业务处理
			String url = request.getUrl();
			if(servletMapping.containsKey(url)) {
				servletMapping.get(url).service(request, response);
			} else {
				response.write("404 - Not Found");
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

	}

}
