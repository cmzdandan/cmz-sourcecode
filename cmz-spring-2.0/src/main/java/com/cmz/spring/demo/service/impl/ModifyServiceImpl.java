package com.cmz.spring.demo.service.impl;

import com.cmz.spring.demo.service.ModifyService;
import com.cmz.spring.formework.annotation.CmzService;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午10:58:15
 * @description 增删改实现类
 */
@CmzService
public class ModifyServiceImpl implements ModifyService {

	public String add(String name, String addr) {
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	public String remove(Integer id) {
		return "modifyService remove,id=" + id;
	}

	public String edit(Integer id, String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}


}
