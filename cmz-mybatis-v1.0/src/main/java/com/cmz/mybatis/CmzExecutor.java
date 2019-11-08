package com.cmz.mybatis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cmz.pojo.Blog;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年10月8日 下午3:15:46
 * @description Executor：数据库的实际操作者
 */
public class CmzExecutor {

	@SuppressWarnings("unchecked")
	public <T> T query(String sql, Object parameter) {
		Connection conn = null;
        Statement stmt = null;
        Blog blog = new Blog();
        try {
        	// 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 打开连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cmz-mybatis", "root", "123456");
            // 执行查询
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(sql, parameter));
            // 获取结果集(把结果封装到Blog对象中)
            while(rs.next()) {
            	Integer bid = rs.getInt("bid");
                String name = rs.getString("name");
                Integer authorId = rs.getInt("author_id");
                blog.setAuthorId(authorId);
                blog.setBid(bid);
                blog.setName(name);
            }
            rs.close();
            stmt.close();
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return (T) blog;
	}

}
