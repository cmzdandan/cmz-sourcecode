package com.cmz.orm.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月16日 上午10:52:54
 * @description 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	private DynamicDataSourceEntry dataSourceEntry;

	@Override
	protected Object determineCurrentLookupKey() {
		return this.dataSourceEntry.get();
	}

	public void setDataSourceEntry(DynamicDataSourceEntry dataSourceEntry) {
		this.dataSourceEntry = dataSourceEntry;
	}

	public DynamicDataSourceEntry getDataSourceEntry() {
		return this.dataSourceEntry;
	}

}
