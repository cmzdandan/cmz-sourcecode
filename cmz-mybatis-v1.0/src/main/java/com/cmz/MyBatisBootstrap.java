package com.cmz;

import com.cmz.mapper.BlogMapper;
import com.cmz.mybatis.CmzConfiguration;
import com.cmz.mybatis.CmzExecutor;
import com.cmz.mybatis.CmzSqlSession;
import com.cmz.pojo.Blog;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午4:35:38
 * @description 引导程序
 */
public class MyBatisBootstrap {

	public static void main(String[] args) {
		// 创建一个 SqlSession 对象
		CmzSqlSession sqlSession = new CmzSqlSession(new CmzConfiguration(), new CmzExecutor());
		// 获取一个BlogMapper的代理对象
		BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
		// 这里调用 selectBlogById() 方法，实际上是调用代理对象的 invoke()方法
		// MapperProxy对象持有SqlSession对象，invoke()方法内部直接调用SqlSession对象的操作数据库的方法实现增删改查操作
		// SqlSession对象操作数据库是调用Executor对象来实现的，对数据库真正的增删改查操作都是在Executor对象内来完成的
		Blog blog = blogMapper.selectBlogById(1);
		System.out.println(blog);
	}

}
