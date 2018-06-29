package com.sorm.core;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sorm.bean.ColumnInfo;
import com.sorm.bean.TableInfo;
import com.sorm.po.Person;
import com.sorm.po.User;
import com.sorm.utils.JavaFileUtils;
import com.sorm.utils.ReflectUtils;
import com.sorm.utils.StringUtils;

/**
 * 管理数据库表结构和类结构关系，并根据表结构生成类结构
 * @author Administrator
 *
 */
public class TableContext {
	
	public static Map<String ,TableInfo> tables = new HashMap<>();
	
	public static Map<Class,TableInfo> poClassTableMap = new HashMap<>();

	private TableContext() {}
	
	static {
		//初始化tables
		init();
		//更新类结构
//		updateJavaPOFile();
		//加载po包下的类
		loadPOTables();
	}
	
	private static void init() {
		try {
			Connection conn = DBManager.getConn();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet tableSet = dbmd.getTables(null, "%", "%", new String[] {"Table"});
			updateFieldTables(conn, dbmd, tableSet);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void updateFieldTables(Connection conn,DatabaseMetaData dbmd,ResultSet tableSet) {
		try {
			while(tableSet.next()) {
				String tableName = (String)tableSet.getObject("TABLE_NAME");
				TableInfo table = new TableInfo(tableName,new HashMap<String,ColumnInfo>(),new ArrayList<ColumnInfo>());
				tables.put(tableName,table);
				ResultSet columnSet = dbmd.getColumns(null, "%",tableName,  "%");
				while(columnSet.next()) {
					String columnName = (String)columnSet.getObject("COLUMN_NAME");
					String typeName = (String) columnSet.getObject("TYPE_NAME");
					ColumnInfo column = new ColumnInfo(columnName,typeName,0);
					table.getColumns().put(columnName, column);
				}
				ResultSet priKeySet = dbmd.getPrimaryKeys(null,"%", tableName);
				while(priKeySet.next()) {
					String priKeyColumnName = (String)priKeySet.getObject("COLUMN_NAME");
					ColumnInfo priColumn = tables.get(tableName).getColumns().get(priKeyColumnName);
					priColumn.setKeyType(1);
					table.getPriKeys().add(priColumn);
				}
				
				if(table.getPriKeys().size()>0) {
					table.setOnlyPriKey(table.getPriKeys().get(0));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateJavaPOFile() {
		for(TableInfo table:tables.values()) {
			JavaFileUtils.createJavaPOFile(table,new MysqlTypeConvertor());
		}
	}
	
	public static void updateJavaPOFile(Class c) {
		try {
			Connection conn = DBManager.getConn();
			DatabaseMetaData dbmd = conn.getMetaData();
			String className = StringUtils.firstCharToLowerCase(c.getSimpleName());
			ResultSet tableSet = dbmd.getTables(null, "%",className, new String[] {"Table"});
			updateFieldTables(conn, dbmd, tableSet);
			JavaFileUtils.createJavaPOFile(tables.get(className), ConvertorFactory.createConvertor());
			TableContext.loadPOTables(tables.get(className));

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
		}
	}
	/**
	 * 加载po包下面的类
	 */
	private static void loadPOTables() {
		for(TableInfo tableInfo :tables.values()) {
			try {
				Class c = Class.forName(DBManager.getConfiguration().getPoPackage()+"."+StringUtils.firstCharToUpperCase(tableInfo.getTname()));
				poClassTableMap.put(c, tableInfo);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
					
		}
	}
	/*
	 * 加载单个类
	 */
	private static void loadPOTables(TableInfo tableInfo) {
		try {
			Class c = Class.forName(DBManager.getConfiguration().getPoPackage()+"."+StringUtils.firstCharToUpperCase(tableInfo.getTname()));
			poClassTableMap.put(c, tableInfo);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
