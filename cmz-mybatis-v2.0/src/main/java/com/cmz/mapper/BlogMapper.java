package com.cmz.mapper;

import com.cmz.annotation.Entity;
import com.cmz.annotation.Select;
import com.cmz.pojo.Blog;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月9日 下午11:45:50
 * @description Blog的mapper接口
 */
@Entity(Blog.class)
public interface BlogMapper {

	/**
	 * 根据主键查询文章
	 * 
	 * @param bid
	 * @return
	 */
	@Select("select * from blog where bid = ?")
	public Blog selectBlogById(Integer bid);

}
