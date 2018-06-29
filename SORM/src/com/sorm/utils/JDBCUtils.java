package com.sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;


/**
 * 封装了JDBC常用操作
 * @author Administrator
 *
 */
public class JDBCUtils {
	/**
	 * 给sql设置参数
	 * 
	 */
	public static void handleParams(PreparedStatement ps,Object[] params) {
		if(params!=null) {
			for(int i=0;i<params.length;i++) {
				try {
					ps.setObject(i+1, params[i]);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void handleParams(PreparedStatement ps,Map<String,Object>params) {
		if(params!=null) {
			for(int i=0;i<params.values().size();i++) {
				try {
					ps.setObject(i+1, params.values().toArray()[i]);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
