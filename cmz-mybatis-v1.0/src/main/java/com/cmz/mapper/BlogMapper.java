package com.cmz.mapper;

import com.cmz.pojo.Blog;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午3:58:50
 * @description Blog的mapper接口
 */
public interface BlogMapper {

	public Blog selectBlogById(Integer bid);
	
}
