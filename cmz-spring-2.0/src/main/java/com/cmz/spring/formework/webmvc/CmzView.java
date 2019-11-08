package com.cmz.spring.formework.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午5:38:11
 * @description 视图模型
 */
public class CmzView {

	public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

	private File viewFile;

	public CmzView(File viewFile) {
		this.viewFile = viewFile;
	}

	public String getContentType() {
		return DEFAULT_CONTENT_TYPE;
	}

	/**
	 * 视图模型渲染(成字符串，用response对象写到页面)
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");
		try {
			String line = null;
			while (null != (line = ra.readLine())) {
				line = new String(line.getBytes("ISO-8859-1"), "utf-8");
				Pattern pattern = Pattern.compile("$\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					String paramName = matcher.group();
					paramName = paramName.replaceAll("$\\{|\\}", "");
					Object paramValue = model.get(paramName);
					if (null == paramValue) {
						continue;
					}
					// 要把${}中间的这个字符串给取出来
					line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
					matcher = pattern.matcher(line);
				}
				sb.append(line);
			}
		} finally {
			ra.close();
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(sb.toString());
	}

	/**
	 * 处理特殊字符
	 * 
	 * @param string
	 * @return
	 */
	private String makeStringForRegExp(String string) {
		return string.replace("\\", "\\\\").replace("*", "\\*").replace("+", "\\+").replace("|", "\\|")
				.replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("^", "\\^")
				.replace("$", "\\$").replace("[", "\\[").replace("]", "\\]").replace("?", "\\?").replace(",", "\\,")
				.replace(".", "\\.").replace("&", "\\&");
	}

}
