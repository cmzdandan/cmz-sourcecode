package com.cmz.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 下午3:57:07
 * @description 连接点接口
 */
public interface CmzJoinPoint {

	Method getMethod();

	Object[] getArguments();

	Object getThis();

}
