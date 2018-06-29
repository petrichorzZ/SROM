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
 * �����ѯ�������ṩ����ĺ����ࣩ
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
	 * ִ��һ��DML���
	 * 
	 * @param sql
	 *            sql���
	 * @param params
	 *            ����
	 * @return ִ��sql����Ӱ�������
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
	 * ��һ������洢�����ݿ���
	 * 
	 * @param obj
	 */
	public void insert(Object obj) {
		// insert into ���� (id, ename, pwd) values(?,?,?);
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
	 * ɾ��clazz��ʾ���ж�Ӧ�ı��еļ�¼��ָ������ֵid�ļ�¼��
	 * 
	 * @param clazz
	 *            �����Ӧ�����class����
	 * @param id
	 *            ������ֵ
	 */
	public void delete(Class clazz, Object prikeyValue) {
		// Emp.class , id =2 --> delete from emp where id =2 ;
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + " = ?";
		executeDML(sql, new Object[] { prikeyValue });
	}

	/**
	 * ɾ�����������ݿ��ж�Ӧ�ļ�¼���������ڵ����Ӧ���������������ֵ��Ӧ����¼��
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
	 * ���¶����Ӧ�ļ�¼����ֻ����ָ���ֶε�ֵ
	 * 
	 * @param obj
	 *            ��Ҫ���¶���
	 * @param fieldNames
	 *            ���������б�
	 * @return ִ��sql��Ӱ�������
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
	 * ��ѯ���ض��м�¼������ÿ�м�¼��װ��classָ������Ķ�����
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
					// ����
					while (rs.next()) {
						if (result == null) {
							result = new ArrayList<>();
						}
						Object row = clazz.newInstance();
						// ����
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
	 * ��ѯ����һ�м�¼������ÿ�м�¼��װ��classָ������Ķ�����
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
	 * ��ѯ����һ��ֵ��
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
	 * ��ѯ����һ������
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
	 * ��javabeanӳ�䵽���ݿ⣬������ֶ�����ΪString���������������ֶ������ֶγ��Ȼ�primary key �ֱ���Ϊkey�� value
	 * ��װ��map����Ϊ��������params;
	 * @param clazz 
	 * @param params
	 */
	public abstract void createDatabaseTable(Class clazz,Map<String, Object> params);
	
	/**
	 *ɾ���� 
	 */
	public abstract void dropTable(Class clazz);
	/**
	 * �޸ı�
	 */
	
	public abstract void alterTable(Class clazz,String sql,Object[] params);
	
}
