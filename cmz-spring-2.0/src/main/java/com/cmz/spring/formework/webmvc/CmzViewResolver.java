package com.cmz.spring.formework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午5:34:28
 * @description 视图解析器
 *              <p>
 *              设计这个类的主要目的是:
 *              <p>
 *              将一个静态文件变为一个动态文件，根据用户传送参数不同，产生不同的结果，最终输出字符串，交给 Response 输出
 */
public class CmzViewResolver {

	private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

	private File templateRootDir;

	private String viewName;

	public CmzViewResolver(String templateRoot) {
		String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
		this.templateRootDir = new File(templateRootPath);
	}

	/**
	 * 将视图名称解析为一个视图对象(根据视图名称拼接一个路径，然后创建一个.html页面文件)
	 * 
	 * @param viewName
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public CmzView resolveViewName(String viewName, Locale locale) throws Exception {
		this.viewName = viewName;
		if (null == viewName || "".equals(viewName.trim())) {
			return null;
		}
		viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
		// 取出多余的斜杠，创建出这样的一个以 .html 为后缀的文件
		File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
		return new CmzView(templateFile);
	}

	/**
	 * 获取视图名称
	 * 
	 * @return
	 */
	public String getViewName() {
		return viewName;
	}
}
