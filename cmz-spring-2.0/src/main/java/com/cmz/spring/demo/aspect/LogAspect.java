package com.cmz.spring.demo.aspect;

import java.util.Arrays;
import java.util.logging.Logger;

import com.cmz.spring.formework.aop.aspect.CmzJoinPoint;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 上午11:18:00
 * @description 日志增强
 */
public class LogAspect {

	private static final Logger log = Logger.getLogger(LogAspect.class.getName());

	// 在调用一个方法之前，执行 before 方法
	public void before(CmzJoinPoint joinPoint) {
		// 这个方法中的逻辑，是由我们自己写的
		log.info("Invoker Before Method!!!" + "\nTargetObject:" + joinPoint.getThis() + "\nArgs:"
				+ Arrays.toString(joinPoint.getArguments()));
	}

	// 在调用一个方法之后，执行 after 方法
	public void after(CmzJoinPoint joinPoint) {
		log.info("Invoker After Method!!!" + "\nTargetObject:" + joinPoint.getThis() + "\nArgs:"
				+ Arrays.toString(joinPoint.getArguments()));
	}

	// 在调用一个方法出现异常之后，执行 afterThrowing 方法
	public void afterThrowing(CmzJoinPoint joinPoint, Throwable ex) {
		log.info("出现异常" + "\nTargetObject:" + joinPoint.getThis() + "\nArgs:"
				+ Arrays.toString(joinPoint.getArguments()) + "\nThrows:" + ex.getMessage());
	}

}
