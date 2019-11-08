package com.cmz;

import com.cmz.mapper.BlogMapper;
import com.cmz.pojo.Blog;
import com.cmz.session.DefaultSqlSession;
import com.cmz.session.SqlSessionFactory;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 上午11:11:55
 * @description 引导程序
 */
public class MyBatisBootstrap {

	public static void main(String[] args) {
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactory();
		DefaultSqlSession sqlSession = sqlSessionFactory.openSqlSession();
		// 获取  Mapper 接口对象
		BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
		Blog blog = blogMapper.selectBlogById(1);
		
		System.out.println("第一次查询: " + blog);
        System.out.println();
        
        blog = blogMapper.selectBlogById(1);
        System.out.println("第二次查询: " + blog);
	}

}
