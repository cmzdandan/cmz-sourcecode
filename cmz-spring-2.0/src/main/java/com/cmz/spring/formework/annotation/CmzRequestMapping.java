package com.cmz.spring.formework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月22日 上午11:26:34
 * @description RequestMapping注解
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmzRequestMapping {

	String value() default "";

}
