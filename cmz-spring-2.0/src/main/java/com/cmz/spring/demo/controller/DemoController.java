package com.cmz.spring.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmz.spring.demo.service.ModifyService;
import com.cmz.spring.demo.service.QueryService;
import com.cmz.spring.formework.annotation.CmzAutowired;
import com.cmz.spring.formework.annotation.CmzController;
import com.cmz.spring.formework.annotation.CmzRequestMapping;
import com.cmz.spring.formework.annotation.CmzRequestParam;
import com.cmz.spring.formework.webmvc.CmzModelAndView;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午11:04:24
 * @description 增删改Controller层
 */
@CmzController
@CmzRequestMapping("/demo")
public class DemoController {

	@CmzAutowired
	private QueryService queryService;

	@CmzAutowired
	private ModifyService modifyService;

	@CmzRequestMapping("/query.json")
	public CmzModelAndView query(HttpServletRequest request, HttpServletResponse response,
			@CmzRequestParam("name") String name) {
		String result = queryService.query(name);
		return out(response, result);
	}

	@CmzRequestMapping("/add*.json")
	public CmzModelAndView add(HttpServletRequest request, HttpServletResponse response,
			@CmzRequestParam("name") String name, @CmzRequestParam("addr") String addr) {
		String result = modifyService.add(name, addr);
		return out(response, result);
	}

	@CmzRequestMapping("/remove.json")
	public CmzModelAndView remove(HttpServletRequest request, HttpServletResponse response,
			@CmzRequestParam("id") Integer id) {
		String result = modifyService.remove(id);
		return out(response, result);
	}

	@CmzRequestMapping("/edit.json")
	public CmzModelAndView edit(HttpServletRequest request, HttpServletResponse response,
			@CmzRequestParam("id") Integer id, @CmzRequestParam("name") String name) {
		String result = modifyService.edit(id, name);
		return out(response, result);
	}

	private CmzModelAndView out(HttpServletResponse response, String context) {
		try {
			response.getWriter().write(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
