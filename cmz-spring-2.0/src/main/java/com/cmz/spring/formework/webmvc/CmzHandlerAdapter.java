package com.cmz.spring.formework.webmvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmz.spring.formework.annotation.CmzRequestParam;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午4:08:29
 * @description 请求处理的转换器
 */
public class CmzHandlerAdapter {

	/**
	 * 判断是否能处理当前的请求
	 * 
	 * @param handler
	 * @return
	 */
	public boolean supports(Object handler) {
		return (handler instanceof CmzHandlerMapping);
	}

	/**
	 * 对请求进行处理并转换
	 * <p>
	 * 对当前的请求对象转换为一个 CmzHandlerMapping 对象
	 * <P>
	 * 从CmzHandlerMapping对象进行解析，提取出所有的形参列表及位置(HttpServletRequest和HttpServletResponse类型特殊处理)
	 * <p>
	 * 通过request对象getParameterMap()方法，拿到所有的实参列表，对实参列表进行遍历解析
	 * <p>
	 * 解析到的key，从形参列表及位置对应关系的map中拿到参数的位置，构造出实参列表(此时的实参列表已经是与方法的形参列表位置 一一对应)
	 * <p>
	 * 通过CmzHandlerMapping对象拿到method，然后反射调用，拿到结果，对结果类型做判断，如果是ModelAndView类型则封装返回
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public CmzModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		CmzHandlerMapping handlerMapping = (CmzHandlerMapping) handler;
		// 每一个方法有一个参数列表，那么这里保存的是形参列表(形参名称与位置的对应关系)
		Map<String, Integer> paramMapping = new HashMap<>();
		// 将 @CmzRequestParam 修饰的参数提取出来封装到paramMapping
		Annotation[][] paramAnnotations = handlerMapping.getMethod().getParameterAnnotations();
		for (int i = 0; i < paramAnnotations.length; i++) {
			for (Annotation annotation : paramAnnotations[i]) {
				if (annotation instanceof CmzRequestParam) {
					String paramName = ((CmzRequestParam) annotation).value().trim();
					if (!"".equals(paramName)) {
						paramMapping.put(paramName, i);
					}
				}
			}
		}
		// 根据用户请求的参数信息，跟 method 中的参数信息进行动态匹配
		// resp 传进来的目的只有一个：只是为了将其赋值给方法参数，仅此而已
		// 只有当用户传过来的 ModelAndView 为空的时候，才会 new 一个默认的
		// 1. 要准备好这个方法的形参列表(方法重载：形参的决定因素：参数的个数、参数的类型、参数顺序、方法的名字)
		// 这里只处理 Request 和 Response类型的参数
		Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> type = paramTypes[i];
			if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
				paramMapping.put(type.getName(), i);
			}
		}

		// 2、拿到自定义命名参数所在的位置
		// 用户通过 URL 传过来的参数列表(一个key可以对应多个value，例如：www.baidu.com?keyword=boy&keyword=girl)
		// 所以，这个地方的Map里面key是一个String类型，value是一个String[]类型
		@SuppressWarnings("unchecked")
		Map<String, String[]> paramsMap = request.getParameterMap();
		// 3、构造实参列表
		Object[] paramValues = new Object[paramTypes.length];
		for (Map.Entry<String, String[]> param : paramsMap.entrySet()) {
			// 对每一组key=value进行处理
			String[] valuesArray = param.getValue();
			// 将实参数组转换成实参字符串，去掉两边的中括号，也去掉实参中间的空格(最终实参是英文逗号连接起来的字符串)
			String valueString = Arrays.toString(valuesArray).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
			if (!paramMapping.containsKey(param.getKey())) {
				continue;
			}
			// 取到当前实参在形参列表的位置
			Integer index = paramMapping.get(param.getKey());
			// 因为页面上传过来的值都是 String 类型的，而在方法中定义的类型是千变万化的，所以，要针对我们传过来的参数进行类型转换
			paramValues[index] = caseStringValue(valueString, paramTypes[index]);
		}
		// 针对 HttpServletRequest 类型进行处理
		if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
			Integer index = paramMapping.get(HttpServletRequest.class.getName());
			paramValues[index] = request;
		}
		// 针对 HttpServletResponse 类型进行处理
		if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
			Integer index = paramMapping.get(HttpServletResponse.class.getName());
			paramValues[index] = response;
		}
		// 4、从 handler 中取出 controller、method，然后利用反射机制进行调用
		Object controller = handlerMapping.getController();
		Method method = handlerMapping.getMethod();
		Object result = method.invoke(controller, paramValues);
		if (result == null) {
			return null;
		}
		boolean isModelAndView = handlerMapping.getMethod().getReturnType() == CmzModelAndView.class;
		if (isModelAndView) {
			return (CmzModelAndView) result;
		} else {
			return null;
		}
	}

	/**
	 * 参数类型转换
	 * 
	 * @param value
	 * @param class1
	 * @return
	 */
	private Object caseStringValue(String value, Class<?> clazz) {
		if (clazz == String.class) {
			return value;
		} else if (clazz == Integer.class) {
			return Integer.valueOf(value);
		} else if (clazz == int.class) {
			return Integer.valueOf(value).intValue();
		} else {
			return null;
		}
	}

}
