package com.cmz.spring.demo.controller;

import java.util.HashMap;
import java.util.Map;

import com.cmz.spring.demo.service.QueryService;
import com.cmz.spring.formework.annotation.CmzAutowired;
import com.cmz.spring.formework.annotation.CmzController;
import com.cmz.spring.formework.annotation.CmzRequestMapping;
import com.cmz.spring.formework.annotation.CmzRequestParam;
import com.cmz.spring.formework.webmvc.CmzModelAndView;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午11:09:14
 * @description 查看页面效果的Controller
 */
@CmzController
@CmzRequestMapping("/")
public class PageController {

	@CmzAutowired
	private QueryService queryService;

	@CmzRequestMapping("/first.html")
	public CmzModelAndView query(@CmzRequestParam("keyword") String keyword) {
		String result = queryService.query(keyword);
		Map<String, Object> model = new HashMap<>();
		model.put("keyword", keyword);
		model.put("data", result);
		model.put("token", "123456");
		return new CmzModelAndView("first.html", model);
	}

}
