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
			System.out.println("��ʼ�����ӳأ����е���������"+pool.size());
		}
	}
	
	public DBConnPool() {
		initPool();
	}
	
	
	/**
	 * �����ӳ���ȡ����
	 * @return
	 */
	public synchronized Connection getConnection() {
		
		Connection conn = pool.get(0);
		pool.remove(0);
		return conn;
	}
	
	/**
	 * �����ӷŻس���
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
