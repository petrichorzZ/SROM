package com.sorm.core;

public class QueryFactory{
	private static Query prototypeObj;//原型对象
	
	static {
		
		try {
			Class.forName("com.sorm.core.TableContext");
			Class c = Class.forName(DBManager.getConfiguration().getQueryClass());
			prototypeObj = (Query)c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private QueryFactory() {
	}
	
	public static Query createQuery() {
		try {
			return (Query) prototypeObj.clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
