package com.sorm.utils;
/**
 * ��װ���ַ������ò���
 * @author Administrator
 *
 */
public class StringUtils {
	public static String firstCharToUpperCase(String str) {
		return str.toUpperCase().substring(0, 1)+str.substring(1);
	}
	
	public static String firstCharToLowerCase(String str) {
		return str.toLowerCase().substring(0,1)+str.substring(1);
	}
	
//	public static boolean isDi
}
