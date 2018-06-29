package com.sorm.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sorm.core.DBManager;

public class DBConnPool {
	private static List<Connection> pool;
	private static final int POOL_MAX_SIZE =DBManager.getConfiguration().getPoolMaxSize();
	private static final int POOL_MIN_SIZE =DBManager.getConfiguration().getPoolMinSize();
	public void initPool() {
		if(pool==null) {
			pool = new ArrayList<>();
		}
		
		while(pool.size()<DBConnPool.POOL_MIN_SIZE) {
			pool.add(DBManager.createConn());
			System.out.println("初始化连接池，池中的连接数："+pool.size());
		}
	}
	
	public DBConnPool() {
		initPool();
	}
	
	
	/**
	 * 从连接池里取连接
	 * @return
	 */
	public synchronized Connection getConnection() {
		
		Connection conn = pool.get(0);
		pool.remove(0);
		return conn;
	}
	
	/**
	 * 将连接放回池中
	 * @param conn
	 */
	public synchronized void close(Connection conn) {
		if(pool.size()>=POOL_MAX_SIZE) {
			try {
				if(conn!=null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			pool.add(conn);
		}
	}

}
