package com.cmz.netty.tomcat.http;

import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月29日 下午4:29:49
 * @description request请求
 */
public class CmzRequest {

	private ChannelHandlerContext context;

	private HttpRequest request;

	public CmzRequest(ChannelHandlerContext context, HttpRequest request) {
		this.context = context;
		this.request = request;
	}
	
	public String getUrl() {
		return request.getUri();
	}
	
	public String getMethod() {
		return request.getMethod().name();
	}
	
	public Map<String, List<String>> getParameters() {
		QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
		return decoder.parameters();
	}
	
	public String getParameter(String name) {
		Map<String, List<String>> params = getParameters();
		List<String> param = params.get(name);
		return param == null ? null : param.get(0);
	}
	
}
