# 手写MyBatis-V1.0

MyBatis是一套非常优秀的ORM框架，功能强大，操作简单。下面通过手写一套乞丐版MyBatis，实现简单的对数据库的查询功能，加深对MyBatis原理的学习和掌握。

### 1.创建一个Maven工程，引入mysql依赖

```
	<dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
    </dependencies>
```

### 2.创建数据库及表

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

### 3.创建包及相关类文件

- 创建com.cmz.pojo包，创建 Blog.java 类文件，该文件是一个实体类，用来封装数据库对应的表数据；

- 创建com.cmz.mapper包，创建 BlogMapper.java 接口文件，这是一个对Blog数据进行操作的接口封装；

- 创建com.cmz.mybatis包，这个包下面都是我们的MyBatis的核心框架类；

  - `CmzSqlSession`是我们对外提供的操作数据库的API，也就是我们的框架给用户使用的API；该对象主要的两大功能：
    - 获取一个接口的代理对象；[实际上是通过持有 `Configuration`对象来获取代理对象]
    - 执行对数据库的增删改查操作；[实际上是通过持有 `Executor` 对象来执行对数据库的操作]

  - `CmzConfiguration`是我们的配置类对象，该对象主要的两大功能
    - 解析配置文件，得到最终的SQL语句；[在框架启动的时候，解析配置文件(只解析一次)]
    - 通过JDK的动态代理，创建Mapper接口的代理对象；
  - `CmzMapperProxy` 是Mapper接口的代理类，继承自InvocationHandler接口
    - 持有 `CmzSqlSession `对象，在 `invoke()` 方法中调用 `CmzSqlSession `对象的方法执行对数据库的增删改查操作
  - `CmzExecutor` 是真正封装对数据库增删改查操作的类

- 在 src/main/resources 目录下创建 sql.properties 文件，K-V键值对确定如何找到sql语句

  > 说明：我们这里为了简化解析SQL，就将SQL写入到了一个 properties 文件中。

- 创建测试类 MyBatisBootstrap 进行测试，效果如下

  ![cmz-mybatis-result](https://github.com/cmzdandan/cmz-mybatis-v1.0/blob/master/src/main/image/cmz-mybatis-result_20191008183204.png)

### 4.最终框架如下

![cmz-mybatis-framework](https://github.com/cmzdandan/cmz-mybatis-v1.0/blob/master/src/main/image/cmz-mybatis-framework_20191008183127.png)

### 5.项目github地址

https://github.com/cmzdandan/cmz-mybatis-v1.0.git

