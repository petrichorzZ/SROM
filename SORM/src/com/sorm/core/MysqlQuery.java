package com.sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sorm.bean.ColumnInfo;
import com.sorm.bean.TableInfo;
import com.sorm.po.Emp;
import com.sorm.utils.JDBCUtils;
import com.sorm.utils.ReflectUtils;
import com.sorm.utils.StringUtils;
import com.sorm.vo.EmpVo;
/**
 * 针对负责mysql数据库的查询
 * @author Administrator
 *
 */
public class MysqlQuery extends Query{

	
	@Override
	public Object queryPagenate(int pageNum, int size) {
		return null;
	}

	@Override
	public void createDatabaseTable(Class clazz, Map<String, Object> params) {
		Connection conn = DBManager.getConn();
		boolean isTableExist = false;
		PreparedStatement ps =null;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			String className =clazz.getSimpleName();
			ResultSet tableSet = dbmd.getTables(null, "%", className, new String[] {"Table"});
			int i=0;
			while(tableSet.next()) {
				i++;
			}
			if(i==0) {
				String sql = ReflectUtils.createDatabaseTables(clazz,params);
				ps = conn.prepareStatement(sql);
				JDBCUtils.handleParams(ps, params);
				System.out.println(ps);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBManager.close(ps,conn);
			
		}
	
	}

	@Override
	public void dropTable(Class clazz) {
		Connection conn = DBManager.getConn();
		PreparedStatement ps =null;
		String sql;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet tableSet = dbmd.getTables(null, "%", "%", new String[] {"Table"});
			while(tableSet.next()) {
				String tableName = (String)tableSet.getObject("TABLE_NAME");
				if(tableName.equalsIgnoreCase(clazz.getSimpleName())) {
					sql = "drop table "+tableName;
					ps = conn.prepareStatement(sql);
					ps.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBManager.close(ps,conn);
		}
		
	}

	@Override
	public void alterTable(Class clazz,String sql,Object[] params) {
		Connection conn = DBManager.getConn();
		PreparedStatement ps =null;
		try {
			ps =conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			ps.execute();
			TableContext.updateJavaPOFile(clazz);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.close(ps, conn);
		}
	}



}
