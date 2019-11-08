package com.cmz.spring.demo.service;
/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月23日 下午10:56:56
 * @description 增删改业务
 */
public interface ModifyService {

	public String add(String name, String addr) ;
	
	public String remove(Integer id);
	
	public String edit(Integer id, String name);
	
}
