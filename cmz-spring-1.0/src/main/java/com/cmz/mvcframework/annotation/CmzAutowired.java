package com.cmz.mvcframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年5月9日 下午10:59:22
 * @description Autowired注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmzAutowired {

	String value() default "";
	
}
