package com.cmz.orm.demo.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.cmz.orm.demo.entity.Member;
import com.cmz.orm.framework.BaseDaoSupport;
import com.cmz.orm.framework.QueryRule;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月16日 上午11:03:36
 * @description memberDao
 */
@Repository
public class MemberDao extends BaseDaoSupport<Member, Long> {

	@Override
	protected String getPKColumn() {
		return "id";
	}

	@Resource(name = "dataSource")
	@Override
	protected void setDataSource(DataSource dataSource) {
		super.setDataSourceReadOnly(dataSource);
		super.setDataSourceWrite(dataSource);
	}

	public List<Member> selectAll() throws Exception {
		QueryRule queryRule = QueryRule.getInstance();
		queryRule.andLike("name", "Tom%");
		return super.select(queryRule);
	}

}
