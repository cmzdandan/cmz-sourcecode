package com.cmz.orm.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月16日 上午11:01:05
 * @description order实体类
 */
@Entity
@Table(name = "t_order")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	@Column(name = "memberId")
	private Long memberId;

	private String detail;

	private Long createTime;

	private String createTimeFmt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeFmt() {
		return createTimeFmt;
	}

	public void setCreateTimeFmt(String createTimeFmt) {
		this.createTimeFmt = createTimeFmt;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", memberId=" + memberId + ", detail=" + detail + ", createTime=" + createTime
				+ ", createTimeFmt=" + createTimeFmt + "]";
	}

}
