package com.sorm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import com.sorm.core.ConvertorFactory;
import com.sorm.core.DBManager;
import com.sorm.core.MysqlTypeConvertor;
import com.sorm.core.TypeConvertor;
import com.sorm.po.User;

/**
 * 封装了反射常用操作
 * @author Administrator
 *
 */
public class ReflectUtils {
	public static Object invokeGet(String fieldName,Object obj) {
		try {
			Class c = obj.getClass();
			fieldName = StringUtils.firstCharToUpperCase(fieldName);
			Method m = c.getDeclaredMethod("get"+fieldName, null);
			return m.invoke(obj, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void invokeSet(String fieldName,Object obj,Object columnValue) {
		try {
			Class c = obj.getClass();
			if(columnValue!=null) {
				Method m = c.getDeclaredMethod("set"+StringUtils.firstCharToUpperCase(fieldName), columnValue.getClass());
				m.invoke(obj, columnValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String createDatabaseTables(Class c,Map<String,Object> keys) {
		TypeConvertor convertor = ConvertorFactory.createConvertor();
		String tableName = StringUtils.firstCharToLowerCase(c.getSimpleName());
		//create table tableName ( id int primary key,name varchar(20));
		StringBuilder sql = new StringBuilder("create table "+tableName+" (");
		Field fields[] = c.getDeclaredFields();
		for(Field field:fields) {
			String fieldName = field.getName();
			System.out.println(field.getType());
			System.out.println(field.getGenericType());
			String fieldType = convertor.javaTypeTodatabaseType(""+field.getType());
			sql.append(fieldName+" "+fieldType);
			if(keys.containsKey(fieldName)) {
				if(keys.get(fieldName) instanceof Number) {
					sql.append("(?)");
				}else {
					sql.append(" "+keys.get(fieldName));
					keys.remove(fieldName);
				}
			}
			sql.append(",");
		}
		sql.setCharAt(sql.length()-1, ')');;
		return sql.toString();
	}
	public static void main(String[] args) {
		Class c = User.class;
		try {
			Field f = c.getDeclaredField("hiredate");
			System.out.println(f.getType());
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
	}
}
