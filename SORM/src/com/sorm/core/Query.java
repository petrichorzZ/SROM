package com.sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sorm.bean.ColumnInfo;
import com.sorm.bean.TableInfo;
import com.sorm.utils.JDBCUtils;
import com.sorm.utils.ReflectUtils;
import com.sorm.utils.StringUtils;

/**
 * 负责查询（对外提供服务的核心类）
 * 
 * @author proxy
 *
 */
public abstract class Query implements Cloneable {

	public Object executeQueryTemplate(String sql, Object[] params, Class clazz, CallBack back) {
		Connection conn = DBManager.getConn();
		Object result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			rs = ps.executeQuery();
			result = back.doExecute(conn, ps, rs);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(rs, ps, conn);
		}

		return null;
	}

	/**
	 * 执行一个DML语句
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数
	 * @return 执行sql语句后影响的行数
	 */
	public int executeDML(String sql, Object[] params) {
		Connection conn = DBManager.getConn();
		int count = 0;
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			count = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(ps, conn);
		}
		return 0;
	}

	/**
	 * 将一个对象存储到数据库中
	 * 
	 * @param obj
	 */
	public void insert(Object obj) {
		// insert into 表名 (id, ename, pwd) values(?,?,?);
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		String tableName = tableInfo.getTname();
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
		ColumnInfo prikey = tableInfo.getOnlyPriKey();
		Field[] fields = c.getDeclaredFields();
		int notNullField = 0;
		for (Field f : fields) {
			String fieldName = f.getName();
			Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
			if (fieldValue != null) {
				notNullField++;
				params.add(fieldValue);
				sql.append(fieldName + ",");
			}
		}
		sql.setCharAt(sql.length() - 1, ')');
		sql.append(" values (");
		for (int i = 0; i < notNullField; i++) {
			sql.append(" ? ,");
		}
		sql.setCharAt(sql.length() - 1, ')');
		executeDML(sql.toString(), params.toArray());
	}

	/**
	 * 删除clazz表示类中对应的表中的记录（指定主键值id的记录）
	 * 
	 * @param clazz
	 *            跟表对应的类的class对象
	 * @param id
	 *            主键的值
	 */
	public void delete(Class clazz, Object prikeyValue) {
		// Emp.class , id =2 --> delete from emp where id =2 ;
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + " = ?";
		executeDML(sql, new Object[] { prikeyValue });
	}

	/**
	 * 删除对象在数据库中对应的记录（对象所在的类对应到表，对象的主键的值对应到记录）
	 * 
	 * @param obj
	 */
	public void delete(Object obj) {
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo columnInfo = tableInfo.getOnlyPriKey();
		String fieldName = StringUtils.firstCharToUpperCase(columnInfo.getName());
		Object prikeyValue = ReflectUtils.invokeGet(fieldName, obj);
		delete(c, prikeyValue);
	}

	/**
	 * 更新对象对应的记录，并只更新指定字段的值
	 * 
	 * @param obj
	 *            所要更新对象
	 * @param fieldNames
	 *            更新属性列表
	 * @return 执行sql后影响的行数
	 */
	public int update(Object obj, String[] fieldNames) {
		// update tableName set ename = ? , age = ? where prikey = ?;
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo prikey = tableInfo.getOnlyPriKey();
		List<Object> params = new ArrayList<>();
		Object prikeyValue = ReflectUtils.invokeGet(prikey.getName(), obj);
		StringBuilder sql = new StringBuilder("update " + tableInfo.getTname() + " set ");
		for (int i = 0; i < fieldNames.length; i++) {
			sql.append(fieldNames[i] + " = ? ,");
			params.add(ReflectUtils.invokeGet(fieldNames[i], obj));
		}
		sql.setCharAt(sql.length() - 1, ' ');
		sql.append("where " + prikey.getName() + " = ?");
		params.add(prikeyValue);
		executeDML(sql.toString(), params.toArray());
		return 0;
	}

	/**
	 * 查询返回多行记录，并将每行记录封装到class指定的类的对象中
	 * 
	 * @param sql
	 * @param clazz
	 * @param params
	 * @return
	 */
	public List queryRows(String sql, Class clazz, Object[] params) {
		return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {

			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				try {
					ResultSetMetaData rsmd = rs.getMetaData();
					List result = null;
					// 多行
					while (rs.next()) {
						if (result == null) {
							result = new ArrayList<>();
						}
						Object row = clazz.newInstance();
						// 多列
						for (int i = 0; i < rsmd.getColumnCount(); i++) {
							String columnName = rsmd.getColumnLabel(i + 1);
							Object columnValue = rs.getObject(i + 1);
							ReflectUtils.invokeSet(columnName, row, columnValue);
						}
						result.add(row);
					}
					return result;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});

	}

	/**
	 * 查询返回一行记录，并将每行记录封装到class指定的类的对象中
	 * 
	 * @param sql
	 * @param clazz
	 * @param params
	 * @return
	 */
	public Object queryUniqueRow(String sql, Class clazz, Object[] params) {
		List list = queryRows(sql, clazz, params);
		return (list == null || list.size() <= 0) ? null : list.get(0);
	}
	
	public Object queryById(Class clazz,Object prikeyValue) {
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		String sql = "select * from "+tableInfo.getTname()+ " where "+onlyPriKey.getName()+" = ?";
		return queryUniqueRow(sql, clazz, new Object[] {prikeyValue});
	}

	/**
	 * 查询返回一个值，
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Object queryValue(String sql, Object[] params) {

		return executeQueryTemplate(sql, params, null, new CallBack() {

			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object result = null;
				try {
					while (rs.next()) {
						result = rs.getObject(1);
					}
					return result;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});
	}

	/**
	 * 查询返回一个数字
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Number queryNumber(String sql, Object[] params) {
		return (Number) queryValue(sql, params);
	}

	public abstract Object queryPagenate(int pageNum, int size);

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	/**
	 * 将javabean映射到数据库，如果类字段属性为String或是主键，将该字段名和字段长度或primary key 分别作为key， value
	 * 封装进map，作为参数。即params;
	 * @param clazz 
	 * @param params
	 */
	public abstract void createDatabaseTable(Class clazz,Map<String, Object> params);
	
	/**
	 *删除表 
	 */
	public abstract void dropTable(Class clazz);
	/**
	 * 修改表
	 */
	
	public abstract void alterTable(Class clazz,String sql,Object[] params);
	
}
