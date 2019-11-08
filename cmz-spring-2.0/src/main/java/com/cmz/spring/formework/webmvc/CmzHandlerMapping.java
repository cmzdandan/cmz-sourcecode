package com.cmz.spring.formework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午3:57:15
 * @description 封装url与handler的映射关系
 */
public class CmzHandlerMapping {

	private Object controller;

	private Method method;
	// url 的封装
	private Pattern pattern;
	
	public CmzHandlerMapping(Pattern pattern,Object controller, Method method) {
		this.pattern = pattern;
		this.controller = controller;
		this.method = method;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

}
