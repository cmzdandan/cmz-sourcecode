package com.cmz.spring.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmz.spring.demo.service.QueryService;
import com.cmz.spring.formework.annotation.CmzService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午10:54:56
 * @description 查询的实现类
 */
@CmzService
public class QueryServiceImpl implements QueryService {

	@Override
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		return json;
	}

}
