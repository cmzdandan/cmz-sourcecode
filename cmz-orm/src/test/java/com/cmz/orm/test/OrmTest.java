package com.cmz.orm.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmz.orm.demo.dao.MemberDao;
import com.cmz.orm.demo.dao.OrderDao;
import com.cmz.orm.demo.entity.Member;
import com.cmz.orm.demo.entity.Order;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年7月16日 下午2:03:28
 * @description Orm框架的单元测试
 *              <p>
 *              ORM（对象关系映射 Object Relation Mapping）
 *              <p>
 * 				Hibernate/Spring JDBC/MyBatis/JPA 一对多、多对多、一对一
 */
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class OrmTest {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmdd");

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private OrderDao orderDao;

	@Test
	public void testSelectAllForMember() {
		try {
			List<Member> memberList = memberDao.selectAll();
			System.out.println(Arrays.toString(memberList.toArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInsertMember() {
		try {
			for (int age = 25; age < 35; age++) {
				Member member = new Member();
				member.setAge(age);
				member.setName("Cmz-" + age);
				member.setAddr("Guangdong Shenzhen");
				memberDao.insert(member);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInsertOrder() {
		try {
			Order order = new Order();
			order.setMemberId(1L);
			order.setDetail("历史订单");
			Date date = sdf.parse("20180201123456");
			order.setCreateTime(date.getTime());
			orderDao.insertOne(order);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
