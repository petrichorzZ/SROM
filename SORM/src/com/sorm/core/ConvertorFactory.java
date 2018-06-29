package com.sorm.core;

public class ConvertorFactory {
	private static TypeConvertor convertor;
	private static String type =DBManager.getConfiguration().getUsingDB().toLowerCase();
	private ConvertorFactory() {}
	public  static TypeConvertor createConvertor() {
		if(convertor==null) {
			synchronized(ConvertorFactory.class) {
				if(convertor==null) {
					switch(type) {
					 case "mysql":convertor =new MysqlTypeConvertor();break;
					}
				}
			}
		}
		return convertor;
	}
}
