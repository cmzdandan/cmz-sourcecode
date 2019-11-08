package com.cmz.orm.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月15日 上午9:44:29
 * @description Class映射，根据Class获取到对于的字段名，get、set方法的提取
 */
public class ClassMappings {

	private ClassMappings() {
	}

	static final Set<Class<?>> SUPPORTED_SQL_OBJECTS = new HashSet<>();

	static {
		// 只要这里写了的，默认支持自动类型转换
		Class<?>[] classes = { boolean.class, Boolean.class, short.class, Short.class, int.class, Integer.class,
				long.class, Long.class, float.class, Float.class, double.class, Double.class, String.class, Date.class,
				Timestamp.class, BigDecimal.class };
		SUPPORTED_SQL_OBJECTS.addAll(Arrays.asList(classes));
	}

	static boolean isSupportedSQLObject(Class<?> clazz) {
		return clazz.isEnum() || SUPPORTED_SQL_OBJECTS.contains(clazz);
	}

	public static Map<String, Method> findPublicGetters(Class<?> clazz) {
		Map<String, Method> map = new HashMap<>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			// 判断该方法是否为静态方法
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			// 方法无参则继续下一个循环
			if (method.getParameterTypes().length != 0) {
				continue;
			}
			Class<?> returnType = method.getReturnType();
			// 无返回值则继续下一个循环
			if (void.class.equals(returnType)) {
				continue;
			}
			// 不在支持的范围内则继续下一个循环
			if (!isSupportedSQLObject(returnType)) {
				continue;
			}
			if ((returnType.equals(boolean.class) || returnType.equals(Boolean.class))
					&& method.getName().startsWith("is") && method.getName().length() > 2) {
				map.put(getGetterName(method), method);
				continue;
			}
			if (!method.getName().startsWith("get")) {
				continue;
			}
			if (method.getName().length() < 4) {
				continue;
			}
			map.put(getGetterName(method), method);
		}
		return map;
	}

	public static Field[] findFields(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	public static Map<String, Method> findPublicSetters(Class<?> clazz) {
		Map<String, Method> map = new HashMap<String, Method>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()))
				continue;
			if (!void.class.equals(method.getReturnType()))
				continue;
			if (method.getParameterTypes().length != 1)
				continue;
			if (!method.getName().startsWith("set"))
				continue;
			if (method.getName().length() < 4)
				continue;
			if (!isSupportedSQLObject(method.getParameterTypes()[0])) {
				continue;
			}
			map.put(getSetterName(method), method);
		}
		return map;
	}

	/**
	 * 根据方法对象获取setter方法的名字
	 * 
	 * @param method
	 * @return
	 */
	public static String getSetterName(Method method) {
		String name = method.getName().substring(3);
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * 根据方法对象获取getter方法的名字(去掉get、is开头然后将首字母转小写)
	 * 
	 * @param method
	 * @return
	 */
	public static String getGetterName(Method method) {
		String name = method.getName();
		if (name.startsWith("is")) {
			name = name.substring(2);
		} else {
			name = name.substring(3);
		}
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

}
