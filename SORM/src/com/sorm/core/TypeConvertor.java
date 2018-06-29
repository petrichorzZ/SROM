package com.sorm.core;
/**
 * 负责java数据类型和数据库数据类型的相互转换
 * @author Administrator
 *
 */
public interface TypeConvertor {
	/**
	 * 数据库类型转Java类型
	 * @param columnType
	 * @return
	 */
	public String databaseTypeToJavaType(String dataType);
	/**
	 * Java类型转数据库类型
	 * @param javaType
	 * @return
	 */
	public String javaTypeTodatabaseType(String javaType);
}
