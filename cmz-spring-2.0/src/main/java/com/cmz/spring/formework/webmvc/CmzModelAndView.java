package com.cmz.spring.formework.webmvc;

import java.util.Map;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午4:02:12
 * @description 封装视图与数据模型的对应关系
 */
public class CmzModelAndView {

	private String viewName;
	private Map<String, ?> model;

	public CmzModelAndView(String viewName) {
		this(viewName, null);
	}

	public CmzModelAndView(String viewName, Map<String, ?> model) {
		this.viewName = viewName;
		this.model = model;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Map<String, ?> getModel() {
		return model;
	}

	public void setModel(Map<String, ?> model) {
		this.model = model;
	}

}
