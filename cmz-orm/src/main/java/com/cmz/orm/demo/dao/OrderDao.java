package com.cmz.orm.demo.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.cmz.orm.datasource.DynamicDataSource;
import com.cmz.orm.demo.entity.Order;
import com.cmz.orm.framework.BaseDaoSupport;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月16日 上午11:07:34
 * @description TODO 用一句话描述该类的作业
 */
@Repository
public class OrderDao extends BaseDaoSupport<Order, Long> {

	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat fullDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DynamicDataSource dataSource;

	@Override
	protected String getPKColumn() {
		return "id";
	}

	@Resource(name = "dynamicDataSource")
	@Override
	protected void setDataSource(DataSource dataSource) {
		this.dataSource = (DynamicDataSource) dataSource;
		this.setDataSourceReadOnly(dataSource);
		this.setDataSourceWrite(dataSource);
	}

	public boolean insertOne(Order order) throws Exception {
		// 约定优于配置
		Date date = null;
		if (order.getCreateTime() == null) {
			date = new Date();
			order.setCreateTime(date.getTime());
		} else {
			date = new Date(order.getCreateTime());
		}
		Integer dbRouter = Integer.valueOf(yearFormat.format(date));
		System.out.println("自动分配到【DB_" + dbRouter + "】数据源");
		this.dataSource.getDataSourceEntry().set(dbRouter);
		order.setCreateTimeFmt(fullDataFormat.format(date));
		Long orderId = super.insertAndReturnId(order);
		order.setId(orderId);
		return orderId > 0;
	}

}
