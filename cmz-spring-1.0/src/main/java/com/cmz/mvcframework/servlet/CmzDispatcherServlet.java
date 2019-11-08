package com.cmz.mvcframework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmz.mvcframework.annotation.CmzAutowired;
import com.cmz.mvcframework.annotation.CmzController;
import com.cmz.mvcframework.annotation.CmzRequestMapping;
import com.cmz.mvcframework.annotation.CmzRequestParam;
import com.cmz.mvcframework.annotation.CmzService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年5月9日 下午9:49:07
 * @description Servlet转发核心类
 */
public class CmzDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// 由于我们是properties文件来代表的配置文件，所以这里声明一个Properties类型全局变量来保持我们的配置信息
	private Properties contextConfig = new Properties();

	// 用于保存各个类的类名
	List<String> classNames = new ArrayList<>();

	// 准备一个ioc容器，为了简化逻辑，我们暂时不考虑ConcurrentHashMap
	Map<String, Object> ioc = new HashMap<>();

	// 准备一个handlerMapping容器，保存url和method对应关系
	// Map<String, Method> handlerMapping = new HashMap<>();
	// 这里不用Map的原因？
	// 因为：我们定义的HandlerMapping这个类，已经封装了url属性，那么就已经具备映射关系了，不需要Map<url,method>映射
	// 根据设计原则：单一职责、最小知道原则，已经有了映射关系，就不需要再次映射
	List<HandlerMapping> handlerMapping = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 调用
		try {
			doDispatch(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("Error code 500, server exception: " + Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 * 运行阶段
	 * 
	 * @param req
	 * @param resp
	 */
	@SuppressWarnings("unchecked")
	private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		HandlerMapping handlerMapping = getHandlerMapping(req);
		if (handlerMapping == null) {
			resp.getWriter().write("404, not found.");
			return;
		}
		// 获取到方法的形参(类型)列表 (这里由于是内部类，所以handlerMapping.paramTypes也可以拿到，为了便于阅读，还是通过get方法拿)
		Class<?>[] paramTypes = handlerMapping.getParamTypes();
		// 构造实参列表，长度就等于形参列表长度。反射的时候需要传值，我们这里就对这个实参列表进行对应的赋值
		Object[] paramValues = new Object[paramTypes.length];
		// 根据请求，拿到用户传递过来的实参，对这些参数进行遍历，并根据参数名找到参数值应该赋值给形参列表的哪个位置，然后就给实参列表对应的赋值
		// url传递过来的实参列表是放在一个map里面的，一个key可以对应多个value，所以其value是一个String数组
		Map<String, String[]> parameterMap = req.getParameterMap();
		// 对url中的参数键值对进行遍历，并赋值给实参列表
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			String param = entry.getKey();
			if (handlerMapping.getParamIndexMapping().containsKey(param)) {
				// 如果我们的映射处理器中存在这个参数名，则取出其对应的形参位置
				Integer index = handlerMapping.getParamIndexMapping().get(param);
				// 取出url带过来的实参，并作相应的类型转换处理
				String[] values = entry.getValue();
				// 所有的实参值是一个一维数组，它们是用中括号括起来的，这里我们进行去除，另外把空格也去掉。得到的结果是：实参值用逗号连接起来的字符串
				String value = Arrays.toString(values).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
				// 根据位置index确定形参类型，做类型转换后赋值给实参列表index位置，这样就完成了url实参与形参列表赋值的过程
				paramValues[index] = convert(paramTypes[index], value);
			}
		}
		// 对类型的参数也进行实参列表赋值
		if (handlerMapping.getParamIndexMapping().containsKey(HttpServletRequest.class.getName())) {
			Integer reqIndex = handlerMapping.getParamIndexMapping().get(HttpServletRequest.class.getName());
			paramValues[reqIndex] = req;
		}
		if (handlerMapping.getParamIndexMapping().containsKey(HttpServletResponse.class.getName())) {
			Integer respIndex = handlerMapping.getParamIndexMapping().get(HttpServletResponse.class.getName());
			paramValues[respIndex] = resp;
		}
		// 上述完成了所有的实参列表赋值，下面就进行方法反射调用
		Object returnValue = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
		if (returnValue == null || returnValue instanceof Void) {
			return;
		}
		resp.getWriter().write(returnValue.toString());
	}

	/**
	 * 根据请求，到容器中找到对应的映射处理器
	 * 
	 * @param req
	 * @return
	 */
	private HandlerMapping getHandlerMapping(HttpServletRequest req) {
		if (handlerMapping.isEmpty()) {
			return null;
		}
		String contextPath = req.getContextPath();
		String url = req.getRequestURI();
		url = url.replaceAll(contextPath, "").replaceAll("/+", "/").replaceAll("\\s", "");
		for (HandlerMapping handlerMap : handlerMapping) {
			if (handlerMap.getUrl().equals(url)) {
				// 在映射处理器中找到了匹配的url，就返回当前匹配上的这个映射处理器
				return handlerMap;
			}
		}
		return null;
	}

	/**
	 * 类型转换器
	 * <p>
	 * HTTP协议是基于字符串的协议，url传递过来的都是字符串，所以，我们只需要将字符串类型的值转换成其他类型
	 * 
	 * @param type
	 *            类型
	 * @param value
	 *            待转换的值
	 * @return
	 */
	private Object convert(Class<?> type, String value) {
		if (type == String.class) {
			return value;
		}
		if (type == Integer.class) {
			return Integer.valueOf(value);
		}
		if (type == Double.class) {
			return Double.valueOf(value);
		}
		if (type == Float.class) {
			return Float.valueOf(value);
		}
		if (type == Boolean.class) {
			return Boolean.valueOf(value);
		}
		// 后面还可以继续扩展，这里可以想到策略模式了
		return value;
	}

	/**
	 * 初始化阶段
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		// 1.加载配置文件
		loLoadConfig(config.getInitParameter("contextConfigLocation"));
		// 2.扫描相关的类(如果我们用.xml文件做配置文件，这个地方的解析就相对复杂很多了)
		doScanner(contextConfig.getProperty("scanPackage"));
		// 3.初始化扫描到的类，并将其放入到IOC容器中
		doInstance();
		// 4.完成依赖注入
		doAutowired();
		// 5.完成HandlerMapping
		initHandlerMapping();
		// 初始化完成，可以调用了
		System.out.println("Cmz Spring framework init success.");
	}

	/**
	 * 初始化url和Method的一对一映射关系：即初始化映射处理器
	 */
	private void initHandlerMapping() {
		if (ioc.isEmpty()) {
			return;
		}
		// 遍历ioc容器，进行依赖注入
		for (Entry<String, Object> entry : ioc.entrySet()) {
			// String beanName = entry.getKey();
			Object controller = entry.getValue();
			Class<?> clazz = controller.getClass();
			if (clazz.isAnnotationPresent(CmzController.class)) {
				// 对@Controller修饰的类进行进一步的处理，做映射关系
				String baseUrl = "";
				if (clazz.isAnnotationPresent(CmzRequestMapping.class)) {
					CmzRequestMapping cmzRequestMapping = clazz.getAnnotation(CmzRequestMapping.class);
					// 获取@RequestMapping修饰的类自定义的路径，例如：@RequestMapping("/demo")，那么这里就获取到demo这个值
					baseUrl = cmzRequestMapping.value();
				}
				// 接下来就要获取到所有的public修饰的方法上对应的路径，做拼接(这里就不能用getDeclaredMethods)
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(CmzRequestMapping.class)) {
						CmzRequestMapping cmzRequestMapping = method.getAnnotation(CmzRequestMapping.class);
						// 将多个斜杠替换成一个斜杠，正则表达式的写法，斜杠后面写个加号，表示多个斜杠
						String url = (baseUrl + "/" + cmzRequestMapping.value()).replaceAll("/+", "/");
						// handlerMapping集合就存放了controller实例与它里面的每个url和method的对应关系
						handlerMapping.add(new HandlerMapping(url, controller, method));
						System.out.println("Mapped: " + url + "=>" + method);
					}
				}
			}
		}
	}

	/**
	 * 完成依赖注入
	 */
	private void doAutowired() {
		if (ioc.isEmpty()) {
			// 如果ioc容器为空，则无需往后执行了
			return;
		}
		for (Entry<String, Object> entry : ioc.entrySet()) {
			/*
			 * entry.getValue() 就拿到了实例化的那个对象，再拿到其类对象，再拿到所有的属性
			 * Declared:所有的、特定的字段，包括private、protected、default
			 * 正常来说，普通的OOP编程只能拿到public修饰的字段，这里通过反射拿到所有的字段
			 */
			Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
			// 对每个属性进行判断，如果该字段有@Autowired修饰的，则需要进行赋值[DI：依赖注入]
			for (Field field : declaredFields) {
				if (field.isAnnotationPresent(CmzAutowired.class)) {
					CmzAutowired cmzAutowired = field.getAnnotation(CmzAutowired.class);
					// 获取用户自定义的beanName
					String beanName = cmzAutowired.value().trim();
					if ("".equals(beanName)) {
						// 如果用户没有自定义beanName，那么就根据类型注入
						beanName = field.getType().getName();
					}
					// 首字母做小写转换
					beanName = toLowerFirstCase(beanName);
					// 如果是public以外的修饰 符，只要加了@Autowired注解，我们都要强制赋值，这个在反射中叫做：暴力访问
					field.setAccessible(true);
					try {
						// 用反射动态给字段赋值
						field.set(entry.getValue(), ioc.get(beanName));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 完成类的实例化，已经有了类名的List，对它做遍历
	 */
	private void doInstance() {
		if (classNames.isEmpty()) {
			// 如果类文件集合为空，也就是没有找到任何类文件，那就不用往下执行了
			return;
		}
		try {
			for (String className : classNames) {
				// 根据类名创建类对象，后面就可以根据类对象初始化这个类
				Class<?> clazz = Class.forName(className);
				// 思考：什么样的类才需要进行初始化呢？ 加了注解的类需要初始化！我们不会在接口上加注解的。
				// 为了简化代码逻辑，主要体会设计思想，我们这里只举例 @Controller @Service
				if (clazz.isAnnotationPresent(CmzController.class)) {
					Object instance = clazz.newInstance();
					// Spring默认类名首字母小写作为bean的名字，我们自己写一个方法来转换下
					String beanName = toLowerFirstCase(clazz.getSimpleName());
					ioc.put(beanName, instance);
				} else if (clazz.isAnnotationPresent(CmzService.class)) {
					/*
					 * 对于获取bean的名字，有这么几种情况 
					 * 1.默认首字母小写;
					 * 2.自定义bean名字的，例如：@Service("orderService")，自定bean的名字为 orderService;
					 * 这种情况，我们就拿到注解，拿到注解里面自定义的bean名字，如果名字不为空，则这个优先级高于默认的，将默认的覆盖掉 
					 * 3.根据类型自动装配;这里只是举例，所以ioc这个map的value是一个Object，实际上应该是一个数组；
					 * 一个接口可以有多个实现类，其多个实例应该放在一个数组里面
					 */
					CmzService cmzService = clazz.getAnnotation(CmzService.class);
					String beanName = cmzService.value();
					if ("".equals(beanName.trim())) {
						beanName = toLowerFirstCase(clazz.getSimpleName());
					}
					Object instance = clazz.newInstance();
					ioc.put(beanName, instance);
					// 把该类对象下的所有接口遍历一遍：例如：OrderServiceImpl 中有一个属性用@Autowired 修饰的 OrderServiceMapper
					for (Class<?> interfacc : clazz.getInterfaces()) {
						// 一个接口允许被多个类实现，所以这里需要做一个判断
						if (ioc.containsKey(interfacc.getName())) {
							throw new Exception("The " + interfacc.getName() + " is exist.");
						}
						// 使用接口的全类名作为key
						ioc.put(interfacc.getName(), instance);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将字符串的首字母转小写
	 * 
	 * @param simpleName
	 *            待转换的字符串
	 * @return 将首字母转为小写
	 */
	private String toLowerFirstCase(String simpleName) {
		char[] charArray = simpleName.toCharArray();
		if (charArray[0] >= 'A' && charArray[0] <= 'Z') {
			// 如果首字母是大写字母才做转换，否则不转换
			charArray[0] += 32;
		}
		return String.valueOf(charArray);
	}

	/**
	 * 扫描相关的类
	 * 
	 * @param scanPackage
	 */
	private void doScanner(String scanPackage) {
		// scanPackage的值就是 com.cmz.demo，其实就是一个包路径，我们把它转化为文件路，将.替换为/就可以了
		// 下面的url可以把它理解为classpath，其实就是classPath下的com.cmz.demo，也就是一个文件目录
		URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll(".", "/"));
		File classPath = new File(url.getFile());
		// 列出这个路径下的所有文件对象，进行各种判断，如果是目录则递归，如果是文件则处理
		for (File file : classPath.listFiles()) {
			if (file.isDirectory()) {
				// 如果是一个文件夹，我们就递归，自己调用自己，改下入参，把路径进行深入一层[父路径加上本路径]
				doScanner(scanPackage + "." + file.getName());
			} else {
				// 如果不是文件夹，则进行一步处理
				if (!file.getName().endsWith(".class")) {
					// 如果不是一个.class文件，则进行下一个文件的判断
					continue;
				}
				// 这里就得到了类文件的文件名
				String className = scanPackage + "." + file.getName().replace(".class", "");
				// 将所有的类的文件名保存到List中，这样就完成了所有的类文件定位
				classNames.add(className);
			}
		}
	}

	/**
	 * 加载配置文件
	 * 
	 * @param contextConfigLocation
	 *            配置文件的路径
	 */
	private void loLoadConfig(String contextConfigLocation) {
		// 在类路径下找到Spring主配置文件，通过当前类加载器将配置文件加载到内存
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
		try {
			// 并将其读取出来放入到Properties对象中，将相当于把 scanPackage=com.cmz.demo 放入到contextConfig对象中
			contextConfig.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 定义一个类，用于封装请求映射的相关信息
	 * 
	 * @author chen.mz
	 *
	 */
	public class HandlerMapping {
		private String url;
		private Method method;
		private Object controller;
		private Class<?>[] paramTypes;
		// 保存参数的名字和参数的位置的对应关系
		private Map<String, Integer> paramIndexMapping;

		public HandlerMapping(String url, Object controller, Method method) {
			this.url = url;
			this.method = method;
			this.controller = controller;
			// 在构造方法中初始化形参列表，避免在运行时又去反射，降低性能
			this.paramTypes = method.getParameterTypes();
			paramIndexMapping = new HashMap<>();
			putParamIndexMapping(method);
		}

		public String getUrl() {
			return url;
		}

		public Method getMethod() {
			return method;
		}

		public Object getController() {
			return controller;
		}

		public Class<?>[] getParamTypes() {
			return paramTypes;
		}

		public Map<String, Integer> getParamIndexMapping() {
			return paramIndexMapping;
		}

		private void putParamIndexMapping(Method method) {
			// 拿到参数的注解：一个参数可以有多个注解修饰、一个注解也可以修饰多个参数，这就形成一个二维数组
			// 返回的二维数组，长度表示参数个数，每一行就是一个一维数组，这个一维数组的长度是注解个数
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			for (int i = 0; i < parameterAnnotations.length; i++) {
				// 再进一步遍历这个一维数组：即遍历第i个参数的每个注解
				for (Annotation annotation : parameterAnnotations[i]) {
					if (annotation instanceof CmzRequestParam) {
						String paramName = ((CmzRequestParam) annotation).value();
						if (!"".equals(paramName)) {
							paramIndexMapping.put(paramName, i);
						}
					}
				}
			}
			// 提取方法形参列表中HttpServletRequest类型和HttpServletResponse类型的形参名字及对应的位置
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> type = parameterTypes[i];
				if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
					paramIndexMapping.put(type.getName(), i);
				}
			}
		}

	}

}
