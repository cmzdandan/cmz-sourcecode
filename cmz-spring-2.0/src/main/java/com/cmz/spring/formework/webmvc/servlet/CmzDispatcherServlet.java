package com.cmz.spring.formework.webmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmz.spring.formework.annotation.CmzController;
import com.cmz.spring.formework.annotation.CmzRequestMapping;
import com.cmz.spring.formework.context.CmzApplicationContext;
import com.cmz.spring.formework.webmvc.CmzHandlerAdapter;
import com.cmz.spring.formework.webmvc.CmzHandlerMapping;
import com.cmz.spring.formework.webmvc.CmzModelAndView;
import com.cmz.spring.formework.webmvc.CmzView;
import com.cmz.spring.formework.webmvc.CmzViewResolver;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 上午11:19:48
 * @description Servlet 只是作为一个 MVC 的启动入口
 */
public class CmzDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final String LOCATION = "contextConfigLocation";

	// HandlerMapping 是最核心的设计，也是最经典的，它牛 B 到直接干掉了 Struts、Webwork 等 MVC 框架
	private List<CmzHandlerMapping> handlerMappings = new ArrayList<>();

	// 存放处理映射器与处理适配器之间的对应关系
	private Map<CmzHandlerMapping, CmzHandlerAdapter> handlerAdapters = new HashMap<>();

	private List<CmzViewResolver> viewResolvers = new ArrayList<>();

	private CmzApplicationContext context;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doDispatch(req, resp);
		} catch (Exception e) {
			resp.getWriter()
					.write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>"
							+ Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll("\\s", "\r\n")
							+ "<font color='green'><i>Copyright@GupaoEDU</i></font>");
			e.printStackTrace();
		}
	}

	private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 根据用户请求的 URL 来获得一个 Handler
		CmzHandlerMapping handlerMapping = getHandlerMapping(request);
		if (handlerMapping == null) {
			processDispatchResult(request, response, new CmzModelAndView("404"));
			return;
		}
		// 拿到当前请求处理器的适配器
		CmzHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
		// 这一步只是调用方法，得到返回值
		CmzModelAndView mv = handlerAdapter.handle(request, response, handlerMapping);
		// 这一步才是真的输出
		processDispatchResult(request, response, mv);
	}

	/**
	 * 处理请求(创建视图页面、对页面进行数据渲染)
	 * 
	 * @param request
	 * @param response
	 * @param mv
	 * @throws Exception
	 */
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, CmzModelAndView mv)
			throws Exception {
		if (mv == null) {
			return;
		}
		if (viewResolvers == null || viewResolvers.isEmpty()) {
			return;
		}
		for (CmzViewResolver viewResolver : viewResolvers) {
			CmzView view = viewResolver.resolveViewName(mv.getViewName(), null);
			if (view != null) {
				view.render(mv.getModel(), request, response);
				return;
			}
		}
	}

	/**
	 * 从Map<CmzHandlerMapping, CmzHandlerAdapter> 对应关系的集合中拿到请求处理器的适配器
	 * 
	 * @param handlerMapping
	 * @return
	 */
	private CmzHandlerAdapter getHandlerAdapter(CmzHandlerMapping handlerMapping) {
		if (this.handlerAdapters.isEmpty()) {
			return null;
		}
		CmzHandlerAdapter handlerAdapter = this.handlerAdapters.get(handlerMapping);
		if (handlerAdapter.supports(handlerMapping)) {
			return handlerAdapter;
		}
		return null;
	}

	/**
	 * 从处理映射器集合中拿到处理映射器List<CmzHandlerMapping> 中拿到对应当前请求的CmzHandlerMapping
	 * <p>
	 * 通过request拿到url，通过url与handlerMappings里面的正则进行匹配，拿到可以处理当前请求的handlerMapping对象
	 * 
	 * @param request
	 * @return
	 */
	private CmzHandlerMapping getHandlerMapping(HttpServletRequest request) {
		if (this.handlerMappings.isEmpty()) {
			return null;
		}
		String url = request.getRequestURI();
		String contextPath = request.getContextPath();
		// 拿到最终的映射路径，例如：http://localhost:8080/demo/hello，则拿到 /demo/hello
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		for (CmzHandlerMapping handlerMapping : handlerMappings) {
			Matcher matcher = handlerMapping.getPattern().matcher(url);
			if (matcher.matches()) {
				return handlerMapping;
			}
		}
		return null;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 相当于把 IOC 容器初始化了
		context = new CmzApplicationContext(config.getInitParameter(LOCATION));
		// 初始化九大组件
		initStrategies(context);
	}

	/**
	 * 初始化各种策略(MVC九大组件)
	 * 
	 * @param context
	 */
	private void initStrategies(CmzApplicationContext context) {
		// 有九种策略
		// 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
		// 每种策略可以自定义干预，但是最终的结果都是一致
		// ============= 这里说的就是传说中的九大组件 ================

		// 文件上传解析，如果请求类型是 multipart 将通过MultipartResolver 进行文件上传解析
		initMultipartResolver(context);

		// 本地化解析
		initLocaleResolver(context);

		// 主题解析
		initThemeResolver(context);

		// 通过 HandlerMapping，将请求映射到处理器(系统初始化的时候 ，必须实现)
		// HandlerMapping 用来保存 Controller 中配置的 RequestMapping 和 Method 的一个对应关系
		initHandlerMappings(context);

		// 通过 HandlerAdapter 进行多类型的参数动态匹配(系统初始化的时候 ，必须实现)
		initHandlerAdapters(context);

		// 如果执行过程中遇到异常，将交给HandlerExceptionResolver 来解析
		initHandlerExceptionResolvers(context);

		// 直接解析请求到视图名
		initRequestToViewNameTranslator(context);

		// 通过 viewResolver 解析逻辑视图到具体视图实现(通过 ViewResolvers 实现动态模板的解析)(系统初始化的时候 ，必须实现)
		initViewResolvers(context);

		// flash 映射管理器
		initFlashMapManager(context);
	}

	/**
	 * 初始化处理器映射关系
	 * <p>
	 * 将 Controller 中配置的 RequestMapping 和 Method 进行一一对应
	 * <p>
	 * 按照我们通常的理解应该是一个 Map，例如：Map<String,Method> map; map.put(url,method)
	 * 
	 * @param context
	 */
	private void initHandlerMappings(CmzApplicationContext context) {
		// 首先从容器中取到所有的实例
		String[] beanNames = context.getBeanDefinitionNames();
		try {
			for (String beanName : beanNames) {
				// 到了 MVC 层，对外提供的方法只有一个 getBean() 方法,返回的对象不是 BeanWrapper，怎么办？
				Object controller = context.getBean(beanName);
				Class<?> clazz = controller.getClass();
				// 找到被 @CmzController修饰的类，这个就是我们的Controller层
				if (!clazz.isAnnotationPresent(CmzController.class)) {
					continue;
				}
				String baseUrl = "";
				if (clazz.isAnnotationPresent(CmzRequestMapping.class)) {
					CmzRequestMapping requestMapping = clazz.getAnnotation(CmzRequestMapping.class);
					// 类的头上 @CmzRequestMapping 修饰的地方带上的路径，例如:@CmzRequestMapping("/demo")
					baseUrl = requestMapping.value();
				}
				// 扫描所有的 public 方法
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (!method.isAnnotationPresent(CmzRequestMapping.class)) {
						continue;
					}
					// 方法头上@CmzRequestMapping 修饰的地方带上的路径，例如:@CmzRequestMapping("/hello")
					CmzRequestMapping requestMapping = method.getAnnotation(CmzRequestMapping.class);
					String methodUrl = requestMapping.value();
					String regex = ("/" + baseUrl + methodUrl.replaceAll("\\*", ".*")).replaceAll("/+", "/");
					Pattern pattern = Pattern.compile(regex);
					CmzHandlerMapping handlerMapping = new CmzHandlerMapping(pattern, controller, method);
					handlerMappings.add(handlerMapping);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化处理器的适配器
	 * <p>
	 * 在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
	 * <p>
	 * 因为后面用反射调用的时候，传的形参是一个数组
	 * <p>
	 * 可以通过记录这些参数的位置 index，挨个往数组中填值，这样的话，就和参数的顺序无关了
	 * 
	 * @param context
	 */
	private void initHandlerAdapters(CmzApplicationContext context) {
		for (CmzHandlerMapping handlerMapping : handlerMappings) {
			//每一个方法有一个参数列表，那么这里保存的是形参列表
			handlerAdapters.put(handlerMapping, new CmzHandlerAdapter());
		}
	}

	/**
	 * 初始化视图解析器
	 * @param context
	 */
	private void initViewResolvers(CmzApplicationContext context) {
		//在页面敲一个 http://localhost/first.html，解决页面名字和模板文件关联的问题
		String templateRoot = context.getConfig().getProperty("templateRoot");
		// 拿到模板文件根目录的路径名
		String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
		// 创建出模板文件根路径文件对象(目录)
		File templateRootDir = new File(templateRootPath);
		// 遍历模板文件目录
		for (String template : templateRootDir.list()) {
			// 拿到页页面模板文件的名字，再根据名字创建出视图解析器
			viewResolvers.add(new CmzViewResolver(template));
		}
	}

	private void initFlashMapManager(CmzApplicationContext context) {
	}

	private void initRequestToViewNameTranslator(CmzApplicationContext context) {
	}

	private void initHandlerExceptionResolvers(CmzApplicationContext context) {
	}

	private void initThemeResolver(CmzApplicationContext context) {
	}

	private void initLocaleResolver(CmzApplicationContext context) {
	}

	private void initMultipartResolver(CmzApplicationContext context) {
	}

}
