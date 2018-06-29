package com.sorm.core;

public class MysqlTypeConvertor implements TypeConvertor{

	@Override
	public String databaseTypeToJavaType(String dataType) {
		if("char".equalsIgnoreCase(dataType)||"varchar".equalsIgnoreCase(dataType)) {
			return "String";
		}else if(dataType.equalsIgnoreCase("bigint")) {
			return "Long";
		}else if(dataType.toLowerCase().contains("int")) {
			return "Integer";
		}else if("double".equalsIgnoreCase(dataType)) {
			return "Double";
		}else if("float".equalsIgnoreCase(dataType)) {
			return "Float";
		}else if("clob".equalsIgnoreCase(dataType)) {
			return "java.sql.Clob";
		}else if("blob".equalsIgnoreCase(dataType)) {
			return "java.sql.Blob";
		}else if("date".equalsIgnoreCase(dataType)) {
			return "java.sql.Date";
		}else if("time".equalsIgnoreCase(dataType)) {
			return "java.sql.Time";
		}else if("timestamp".equalsIgnoreCase(dataType)) {
			return "java.sql.Timestamp";
		}
		return null;
	}

	@Override
	public String javaTypeTodatabaseType(String javaType) {
		if("class java.lang.String".equals(javaType)) {
			return "varchar";
		}else if("long".equals(javaType)||("class java.lang.Long").equals(javaType)) {
			return "bigint";
		}else if("int".equals(javaType)||"class java.lang.Integer".equals(javaType)) {
			return "int";
		}else if("float".equals(javaType)||"class java.lang.Float".equals(javaType)) {
			return "float";
		}else if("double".equals(javaType)||"class java.lang.Double".equals(javaType)) {
			return "double";
		}else if("class java.sql.Clob".equals(javaType)) {
			return "Clob";
		}else if("class java.sql.Blob".equals(javaType)) {
			return "Blob";
		}else if("class java.sql.Date".equals(javaType)) {
			return "Date";
		}else if("class java.sql.Time".equals(javaType)) {
			return "time";
		}else if("class java.sql.Timestamp".equals(javaType)) {
			return "timestamp";
		}
		return null;
	}

}
