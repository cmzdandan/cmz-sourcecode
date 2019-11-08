package com.cmz.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmz.mvcframework.annotation.CmzAutowired;
import com.cmz.mvcframework.annotation.CmzController;
import com.cmz.mvcframework.annotation.CmzRequestMapping;
import com.cmz.mvcframework.annotation.CmzRequestParam;
import com.cmz.service.DemoService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年5月10日 下午10:17:17
 * @description DemoController
 */
@CmzController
@CmzRequestMapping("/demo")
public class DemoController {

	@CmzAutowired
	private DemoService demoService;

	@CmzRequestMapping("/query")
	public void query(HttpServletRequest request, HttpServletResponse response, @CmzRequestParam("name") String name) {
		String result = "My name is " + name;
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@CmzRequestMapping("/add")
	public void add(HttpServletRequest request, HttpServletResponse response, @CmzRequestParam("a") Integer a,
			@CmzRequestParam("b") Integer b) {
		try {
			response.getWriter().write(a + "+" + b + "=" + (a + b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@CmzRequestMapping("/sub")
	public void add(HttpServletRequest request, HttpServletResponse response, @CmzRequestParam("a") Double a,
			@CmzRequestParam("b") Double b) {
		try {
			response.getWriter().write(a + "-" + b + "=" + (a - b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@CmzRequestMapping("/remove")
	public String remove(@CmzRequestParam("id") Integer id) {
		return "" + id;
	}

}
