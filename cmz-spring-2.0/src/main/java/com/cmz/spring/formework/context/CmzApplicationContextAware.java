package com.cmz.spring.formework.context;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 下午4:41:17
 * @description 通过解耦方式获得 IOC 容器的顶层设计
 *              <p>
 * 				后面将通过一个监听器去扫描所有的类，只要实现了此接口
 *              <p>
 * 				将自动调用 setApplicationContext() 方法，从而将 IOC 容器注入到目标类中
 */
public interface CmzApplicationContextAware {

	void setApplicationContext(CmzApplicationContext applicationContext);

}
