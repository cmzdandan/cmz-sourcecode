package com.cmz.spring.formework.aop;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月30日 上午11:29:03
 * @description AOP相关配置属性的实体封装
 */
public class CmzAopConfig {

	private String pointCut;
	private String aspectBefore;
	private String aspectAfter;
	private String aspectClass;
	private String aspectAfterThrow;
	private String aspectAfterThrowingName;

	public String getPointCut() {
		return pointCut;
	}

	public void setPointCut(String pointCut) {
		this.pointCut = pointCut;
	}

	public String getAspectBefore() {
		return aspectBefore;
	}

	public void setAspectBefore(String aspectBefore) {
		this.aspectBefore = aspectBefore;
	}

	public String getAspectAfter() {
		return aspectAfter;
	}

	public void setAspectAfter(String aspectAfter) {
		this.aspectAfter = aspectAfter;
	}

	public String getAspectClass() {
		return aspectClass;
	}

	public void setAspectClass(String aspectClass) {
		this.aspectClass = aspectClass;
	}

	public String getAspectAfterThrow() {
		return aspectAfterThrow;
	}

	public void setAspectAfterThrow(String aspectAfterThrow) {
		this.aspectAfterThrow = aspectAfterThrow;
	}

	public String getAspectAfterThrowingName() {
		return aspectAfterThrowingName;
	}

	public void setAspectAfterThrowingName(String aspectAfterThrowingName) {
		this.aspectAfterThrowingName = aspectAfterThrowingName;
	}

}
