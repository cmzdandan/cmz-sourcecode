package com.cmz.mvcframework.context;
/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月8日 下午4:59:13
 * @description ApplicationContextAware
 * <p>通过解耦的方式获取IOC容器的顶层设计(观察者模式)</p>
 * <p>后面将通过一个监听器去扫描所有的类，只要实现了该接口，将自动调用setApplicationContext方法</p>
 * <p>从而将IOC容器注入到目标类中</p>
 */
public interface CmzApplicationContextAware {

	void setApplicationContext(CmzApplicationContext applicationContext);
	
}
