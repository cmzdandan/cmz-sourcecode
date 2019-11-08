package com.cmz.service.impl;

import com.cmz.mvcframework.annotation.CmzService;
import com.cmz.service.DemoService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年5月10日 下午10:34:25
 * @description DemoServiceImpl
 */
@CmzService
public class DemoServiceImpl implements DemoService {

	@Override
	public String get(String name) {
		return "My name is " + name;
	}

}
