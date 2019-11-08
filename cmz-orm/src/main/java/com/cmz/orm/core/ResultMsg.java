package com.cmz.orm.core;

import java.io.Serializable;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月14日 下午12:22:16
 * @description 结果集
 */
public class ResultMsg<T> implements Serializable {

	private static final long serialVersionUID = 7091895942521419021L;

	// 状态码，系统的返回码
	private int status;

	// 状态码的解释
	private String msg;

	// 放任意结果
	private T data;

	public ResultMsg() {
	}

	public ResultMsg(int status) {
		this.status = status;
	}

	public ResultMsg(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public ResultMsg(int status, T data) {
		this.status = status;
		this.data = data;
	}

	public ResultMsg(int status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
