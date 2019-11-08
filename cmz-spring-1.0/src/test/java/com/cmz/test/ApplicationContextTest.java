package com.cmz.test;

import com.cmz.mvcframework.context.CmzApplicationContext;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月9日 下午5:28:31
 * @description TODO 用一句话描述该类的作业
 */
public class ApplicationContextTest {

	public static void main(String[] args) {
		CmzApplicationContext context = new CmzApplicationContext("classpath:application.properties");
		try {
			Object demoController = context.getBean("demoController");
			System.out.println(demoController);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
