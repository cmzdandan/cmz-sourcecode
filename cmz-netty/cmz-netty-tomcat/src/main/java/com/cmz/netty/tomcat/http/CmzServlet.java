package com.cmz.netty.tomcat.http;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月29日 下午4:27:43
 * @description TODO 用一句话描述该类的作业
 */
public abstract class CmzServlet {

	public void service(CmzRequest request, CmzResponse response) throws Exception {
		// 由service方法来决定，是调用doGet或者调用doPost
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			doGet(request, response);
		} else {
			doPost(request, response);
		}
	}

	public abstract void doGet(CmzRequest request, CmzResponse response) throws Exception;

	public abstract void doPost(CmzRequest request, CmzResponse response) throws Exception;

}
