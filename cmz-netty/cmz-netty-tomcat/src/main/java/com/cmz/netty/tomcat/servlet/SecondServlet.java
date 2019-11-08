package com.cmz.netty.tomcat.servlet;


import com.cmz.netty.tomcat.http.CmzRequest;
import com.cmz.netty.tomcat.http.CmzResponse;
import com.cmz.netty.tomcat.http.CmzServlet;

public class SecondServlet extends CmzServlet {

	public void doGet(CmzRequest request, CmzResponse response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(CmzRequest request, CmzResponse response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
