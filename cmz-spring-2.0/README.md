# 手写Spring-V2.0
### 整体思路梳理

1. SpringMVC 的入口是 `DispatcherSerlvet`, `DispatcherServlet `的 `init()`方法完成了 IOC 容器的初始化；

2. 我们使用 Spring 的经验中，我们见得最多的是 `ApplicationContext`，似乎 Spring 托管的所有实例 Bean 都可以通过调用 `getBean()`方法来获得；

3. `ApplicationContext `又是从何而来的呢？通过下面的类图关系能有所体现。

   ![extends-relationship](https://github.com/cmzdandan/cmz-spring-2.0/blob/master/src/main/webapp/image/extends-relationship.png)

根据继承关系再深究方法调用能得出一个结论：<font color='red'>在 `Servlet `的 `init() `方法中初始化了 IOC 容器和 SpringMVC 所依赖的九大组件。</font>

#### 框架搭建

1. 创建一个空的maven工程；

2. 在/src/main/resource 目录下创建 application.properties 来代替 application.xml(简化开发)；

3. pom.xml 配置依赖

```xm
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
			<version>2.4</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.7.25</version>
		</dependency>
```

4. 在/src/main/ 目录下创建 webapp/WEB-INF/web.xml 文件，编写 servlet 和 servlet-mapping，主要如下

   ```xml
   <servlet>
   	<servlet-name>cmzmvc</servlet-name>
   	<servletclass>
           com.cmz.spring.formework.webmvc.servlet.CmzDispatcherServlet
       </servlet-class>
   	<init-param>
   		<param-name>contextConfigLocation</param-name>
   		<param-value>classpath:application.properties</param-value>
   	</init-param>
   	<load-on-startup>1</load-on-startup>
   </servlet>
   ```

   

5. 根据web.xml文件中配置的映射，创建我们的`DispatchServlet` 对象

至此，SpringMVC框架的入口有了，后面实现具体的细节。

#### IOC的实现原理

在`DispatchServlet`的`init()`方法中完成IOC容器的初始化，具体做如下的事情：

- <font color='red'>**定位**</font> 	定位配置文件和扫描相关注解

  - 在类路径下找到Spring主配置文件，通过当前类加载器将配置文件加载到内存

    ```java
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
    ```

  - 根据加载到的配置信息扫描相关路径下的 .class 文件，并将这些信息保存到一个 `List` 中，这样就完成了各个组件的定位

- <font color='red'>**加载**</font>     将配置信息载入到内存

  - 根据定位到的组件信息，对各个组件进行反射并解析生成实例对象，这就完成了配置信息的载入。

    - 跟据定位到的 .class文件，对它们进行反射；
    
    - 对标记了`@Controller`  `@RequestMapping` 等各种注解的类进行解析；

- <font color='red'>**注册**</font>     根据载入的信息将对象初始化到 IOC 容器

  - IOC 容器本质上就是一个 `HashMap`，我们需要把反射创建的对象实例 `put()`到容器中，这样就完成了组件的注册

>  总结：IOC容器初始化，就是找到各个类文件，并加载到内存，反射并解析，最后把得到的对象实例维护到一个 `HashMap`中；

当所有的对象被创建出来之后，对象之间的依赖关系还没有建立，类功能增强AOP还需要实现，另外SpringMVC的九大组件也需要一一创建，这就是后面要分析的。

#### DI的实现原理

​	<font color='red'>DI(Dependency Injection)：依赖注入，完成对象之间的依赖关系。</font>

1. 自定义注解 `@Service`  `@Autowired`  `@Controller`  `@RequestMapping`  `@RequestParam`  等；
2. 设计顶层接口  `FactoryBean`  最核心的就是定义 `getBean()` 接口方法；
3. 被反射创建的对象应该封装成为Spring的统一对象，需要定义  `BeanDefinition `  ;
4. 对象如何被反射呢？由 `BeanDefinitionReader`读取对象信息，就是上面说的加载过程；
5. 整个应用都是在 `ApplicationContext`对象的 `refresh()`方法执行后开始一步步启动起来的；

##### 这里再对执行过程做一个小总结：

- 通过``BeanDefinitionReader``构造函数传入配置文件路径；

- 然后读取配置文件到一个 Properties 对象中；

- 根据读取的Properties对象，取定义好的key，拿到包路径，也就是解析出配置信息；

- 对外提供一个 `loadBeanDefinitions()` 方法，加载所有的类对象封装成`BeanDefinition`对象；

- 创建出所有的 `BeanDefinition` 对象，并存放到 `List<BeanDefinition>`，再返回结果。

##### DI依赖注入详解

- <font color='red'>依赖注入的入口是从 `getBean()`方法开始的;</font>

- 根据 `beanName` 从 `beanDefinitionMap` 中拿到 `beanDefinition`;

- 根据 `beanDefinition` 从单例 IOC 容器 `singletonObjects` 中拿到类的实例对象 `instance`，如果没有则反射创建并放入 IOC 容器中；

- 创建 `beanPostProcessor` 对象，用于创建 `beanWrapper`(对instance进行包装) 前后各做一个回调，增强扩展性；

- 对 `instance` 进行包装，得到 `beanWrapper` 对象，将它加入到 `beanWrapperMap` 集合中；

- 根据 `beanName` 和 `instance` 进行依赖注入;

- 最后，返回 `getBean()` 方法想要拿到的结果：<font color='red'>包装过后的实例对象</font>；

> 总结：依赖注入是从 `getBean()`方法开始的，完成对象之间的依赖关系的建立，为对象的各个属性进行设值，如果需要的值对象没有被创建，则通过IOC阶段扫描到的类名集合取到类名进行反射创建，如果该创建的对象又依赖其他的对象，则继续递归查找创建。简单的说就是，递归查找，设值注入(可以解决循环依赖)。

#### SpringMVC的实现原理

1. 定义`HandlerMapping`对象，封装 `Controller` 对象和 `Method`对象的映射关系；

2. 定义`ModelAndView`对象封装视图信息；

3. 定义 `HandlerAdapter` 对请求进行转换并处理；

4. 定义 `View`对象，表示视图模型，包括渲染页面模板的方法；

5. 定义`ViewResolver`对象，根据视图名称解析出一个视图页面；

6. 完善 `DispatchServlet`的 `doDispatch()`方法，实现请求的转发；

7. 完善 `DispatchServlet`的 `init()`方法，先初始化容器，再初始化九大组件；

   ```java
   @Override
   public void init(ServletConfig config) throws ServletException {
   	// 相当于把 IOC 容器初始化了
   	context = new CmzApplicationContext(config.getInitParameter(LOCATION));
   	// 初始化九大组件
   	initStrategies(context);
   }
   ```

8. 编写相关的`Controller`类，进行测试；

> 总结：在系统启动阶段将请求的处理方法与请求处理器维护到一个`HashMap`中，请求到达时找到具体的处理器进行请求处理，细分有请求转发器、请求适配器、视图解析器等各种组件，将职责划分的非常细致。

> SpringMVC工作的基本流程：前端处理器接收请求，适配器找到处理器、完成业务处理返回数据、视图解析器包装数据渲染视图、最后返回视图。

#### AOP的实现原理

1. 简化实现原理，直接在 `application.properties`文件中定义切面相关的内容；

2. 定义 `AopConfig`封装AOP相关的属性内容；

3. 定义 `JoinPoint`接口；

4. 定义 `MethodInterceptor`对方法拦截；

5. 定义 `AspectJAdvice` 对方法增强；

6. 定义 `AopProxy`创建动态代理对象；

7. 将上述内容接入 `getBean()`；

   我们知道，`getBean()`是依赖注入的入口，如果已经完成了依赖注入，我们可以得到对应的`Bean`对象再对它进行功能增强；

8. 接入业务代码，进行功能验证；

> 总结：AOP简单来说就是通过动态代理实现对被拦截的方法进行功能增强。在 `getBean()`方法中对`Bean`对象的目标方法进行增强，前置处理、后置处理、环绕处理、异常处理等。

#### 整体回顾总结

1. `Servlet`从 `DispatcherServlet`作为入口，Spring 从 `ApplicationContext`作为入口，找到并解析配置文件；
2. 根据解析到的配置文件内容，定位并加载各个类文件的位置；
   - `applicationContext.xml`文件中有bean的配置信息，这里就会被定位到类文件并加载到内存；
   - 有的通过`@Bean`注解的对象，也是同样的原理，只是解析方式不同，被定位并加载到内存；
3. 然后将对象信息解析为 `BeanDefinition` 对象放入到 IOC容器中，实际上就是保存到一个`HashMap`集合中；
4. 从 `getBean()`开始，进行依赖注入，实际上就是通过递归的方式建立对象之间的依赖关系；
   - 依赖注入实际上发生在`Bean`第一次调用的时候，而非IOC容器初始化的时候
   - 依赖注入是通过递归查找，设值注入(解决循环依赖)的方式完成的；
5. 在 `DispatcherServlet`的`init()`方法中，先初始化IOC容器，再完成SpringMVC九大组件的初始化；
6. AOP是在`getBean()`方法中，通过动态代理拦截`Bean`的目标方法，实现对目标方法的功能增强；

