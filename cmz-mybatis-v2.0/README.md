# 手写MyBatis-V2.0

MyBatis是一套非常优秀的ORM框架，功能强大，操作简单。下面通过手写一套乞丐版MyBatis，实现简单的对数据库的查询功能，加深对MyBatis原理的学习和掌握。

下面还是在V1.0的基础上进行改进，这份文档会全面描述V2.0的开发过程。

#### 1.创建一个Maven工程，引入mysql依赖

```
	<dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
    </dependencies>
```

#### 2.创建数据库及表

```
-- 创建数据库
CREATE DATABASE `cmz-mybatis`;
USE DATABASE cmz-mybatis;

-- 创建表
CREATE TABLE `blog` (
  `bid` int(4) DEFAULT NULL COMMENT '文章ID',
  `name` varchar(50) DEFAULT NULL COMMENT '文章标题',
  `author_id` int(4) DEFAULT NULL COMMENT '文章作者ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 初始化数据
INSERT INTO blog VALUES(1, 'java从入门到放弃', 10);
INSERT INTO blog VALUES(2, '人人都是产品经理', 5);
```

#### 3.项目结构

![cmz-mybatis-v2.0-structure](https://github.com/cmzdandan/cmz-mybatis-v2.0/blob/master/src/main/image/cmz-mybatis-v2.0-structure.png)

#### 4.实现思路及流程

##### 4.1 使用方式

参考`MyBatisBootstrap`代码，或者我们平时使用MyBatis的原生API用法。

我们通常会先获取一个SqlSession对象，然后通过该对象的`getMapper()`方法来获取一个Mapper接口的代理对象，最后用这个代理对象执行相应的接口方法，实际上执行的是代理对象的`invoke()`方法，在这个方法里面完成对数据库的操作。

- 对用户：暴露SqlSession对象的`getMapper()`方法和操作数据库的方法，例如：`select()` 、`insert()`、`delete()`、`update()`等；
- 在内部：必须完成下面几件事情
  - `Configuration`完成全局配置文件的解析工作
    - 完成接口与实体类的映射关系的维护
    - 完成接口方法与SQL语句的映射关系维护
  - `SqlSession`将具体的数据库操作委托给执行器`Executor`来完成
  - `Executor`需要参数处理部件`ParameterHandler`和结果集处理部件`ResultSetHandler`

由上面的分析，我们得到最重要的几个对象。

##### 4.2 核心对象的创建与分析

- 创建`SqlSessionFactory`对象

  - 在构造方法中初始化`Configuration`对象
  - 对外暴露创建`SqlSession`的方法`openSqlSession()`

- 创建`SqlSession`的默认实现`DefaultSqlSession`

  - 提供`getMapper()`方法的实现，其实调用`Configuration`的`getMapper()`方法
  - 提供各种数据库操作的方法实现，例如`selectOne()`，通过`Configuration`对象维护的接口方法与SQL语句映射获取sql语句，再通过执行器执行该sql语句

- 创建`Executor`接口，提供默认实现`SimpleExecutor`，具体的操作数据库在`StatementHandler`中完成

  - 数据库的操作通常需要封装下面几个步骤
    - 建立连接
    - 创建数据库操作声明
    - 执行具体操作
    - 处理结果集
    - 关闭连接
  - 将创建数据库操作声明时的参数处理提取到`ParameterHandler`对象中进行专门处理
  - 将结果集的处理提取到`ResultSetHandler`对象中进行专门处理
  - 对`Executor`可以做更多的扩展，例如：带缓存功能的`CachingExecutor`

- `Configuration`对象完成的几件事

  - 解析 properties 文件得到 K-V键值对，K是`Mapper`接口全类名.方法名，V是SQL语句和POJO全类型

  - 提取`Mapper`全类名可以`Class.forName(mapperName)`得到`Mapper`接口的类对象

  - 提取POJO信息可以得到POJO的类对象

    - 将`Mapper`接口对象和POJO类对象维护到一个`HashMap`中

      实际上放入`HashMap`里面的value不是pojo类对象，而是对pojo类对象进行了动态创建代理

      ```java
      knownMappers.put(clazz, new MapperProxyFactory<>(clazz, pojo));
      ```

      这样就创建了接口对象与接口代理工厂的映射关系，当我们`getMapper()`时就可以拿到接口对象代理工厂对象，通过这个代理工厂对象就可以创建代理对象

      ```java
      	public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {
      		MapperProxyFactory<?> proxyFactory = knownMappers.get(clazz);
      		if (proxyFactory == null) {
      			throw new RuntimeException("Type: " + clazz + " can not find");
      		}
      		return (T) proxyFactory.newInstance(sqlSession);
      	}
      ```

      我们可以看到代理工厂创建实例时传入了`sqlSession`对象，这样代理对象可以通过持有`sqlSession`对象进而对数据库进行操作，因为`sqlSession`对象持有`Executor`对象，而`Executror`对象是数据库真正的操作对象。

    - 将`Mapper`接口方法和SQL语句维护到一个`HashMap`中

  - 解析 annotation，遍历 .class文件，同样是提取SQL维护对应的关系

##### 4.3 插件的分析与实现

- `Interceptor`接口的定义，拦截器必须实现该接口

  - `intercept()`方法对被拦截对象进行包装
  - `plugin()`方法对被拦截对象进行代理

- `@Intercepts`注解用于标识哪些类是插件

- 在解析插件时，根据插件全类名得到插件类对象，再`newInstance()`实例化插件，再将插件放入插件链中，插件链实际上是一个`ArrayList`

- 在创建执行器的时候会用到插件，这样就起到了拦截作用，对执行器进行层层代理，这样就可以在插件的实现逻辑中改变原有的SQL命令

  ```java
  if (interceptorChain.hasPlugin()) {
      executor = (Executor) interceptorChain.pluginAll(executor);
  }
  ```


#### 5.项目github地址

https://github.com/cmzdandan/cmz-mybatis-v2.0.git
