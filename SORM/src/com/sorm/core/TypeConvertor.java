package com.sorm.core;
/**
 * ����java�������ͺ����ݿ��������͵��໥ת��
 * @author Administrator
 *
 */
public interface TypeConvertor {
	/**
	 * ���ݿ�����תJava����
	 * @param columnType
	 * @return
	 */
	public String databaseTypeToJavaType(String dataType);
	/**
	 * Java����ת���ݿ�����
	 * @param javaType
	 * @return
	 */
	public String javaTypeTodatabaseType(String javaType);
}
