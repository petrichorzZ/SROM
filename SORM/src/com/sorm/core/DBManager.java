package com.sorm.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.sorm.bean.Configuration;
import com.sorm.pool.DBConnPool;

/**
 * 根据配置信息，
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class DBManager {

	private static Configuration conf;
	private static DBConnPool pool;
	static {
		Properties pros = new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
			conf = new Configuration();
			conf.setDriver(pros.getProperty("driver"));
			conf.setPoPackage(pros.getProperty("poPackage"));
			conf.setUrl(pros.getProperty("url"));
			conf.setPwd(pros.getProperty("pwd"));
			conf.setSrcPath(pros.getProperty("srcPath"));
			conf.setUsingDB(pros.getProperty("usingDB"));
			conf.setUser(pros.getProperty("user"));
			conf.setQueryClass(pros.getProperty("queryClass"));
			conf.setPoolMaxSize(Integer.parseInt(pros.getProperty("poolMaxSize")));
			conf.setPoolMinSize(Integer.parseInt(pros.getProperty("poolMinSize")));
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	// 创建连接
	public static Connection createConn() {
		try {
			Class.forName(conf.getDriver());
			return DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Configuration getConfiguration() {
		return conf;
	}

	public static Connection getConn() {
		if (pool == null) {
			pool = new DBConnPool();
		}
		return pool.getConnection();
	}

	public static void close(ResultSet rs, Statement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
//		try {
//			if(conn!=null) {
//				conn.close();
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		pool.close(conn);

	}

	public static void close(Statement ps, Connection conn) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		pool.close(conn);
	}

	public static void close(ResultSet rs, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		pool.close(conn);
	}

	public static void close(Connection conn) {
		pool.close(conn);
	}

}
