package com.sorm.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sorm.core.DBManager;
import com.sorm.core.Query;
import com.sorm.core.QueryFactory;
import com.sorm.po.Emp;
import com.sorm.po.Person;
import com.sorm.po.User;
import com.sorm.utils.JavaFileUtils;
import com.sorm.utils.ReflectUtils;
import com.sorm.vo.EmpVo;

public class Test {
	static Query q =QueryFactory.createQuery();
	static String sql;
	public static void main(String[] args) {
		test6();
	}
	static void test9() {
		sql = "alter table person drop column sal";
		q.alterTable(Person.class, sql,null);
	}
	static void test8() {
		sql = "alter table person add(sal double , pwd varchar(?))";
		q.alterTable(Person.class, sql,new Object[]{20});
	}
	static void test7() {
		q.dropTable(Person.class);
	}
	static void test6() {
		sql = "insert into person (id,name,age,sex,telephone,email) values (3,'курае╛','23','0','1234567890','98765@qq.com')";
		q.executeDML(sql, null);
		
	}
	static void test5() {
		Query q =QueryFactory.createQuery();
		Map<String,Object> keyValue = new HashMap<>();
		keyValue.put("id", "primary key");
		keyValue.put("name", 20);
		keyValue.put("telephone",30);
		keyValue.put("email", 40);
		q.createDatabaseTable(Person.class,keyValue);
	}
	static void test4() {
		Query q =QueryFactory.createQuery();
		Map<String,Object> keyValue = new HashMap<>();
		keyValue.put("id", " primary key");
		keyValue.put("name", 20);
		keyValue.put("pwd",30);
		keyValue.put("address", 40);
		q.createDatabaseTable(User.class,keyValue);
	}
	
	static void test3() {
		Query q =QueryFactory.createQuery();
		String sql = "select * from emp where id = ?";
		Emp emp = (Emp) q.queryById(Emp.class, 1);
		System.out.println(emp.getEname());
	}
	static void test1() {
		Query q = QueryFactory.createQuery();
		String sql = "select e.id ,e.ename, salary*12 annualSal ,age,d.dname deptName ,d.address deptAddr from emp e join dept d ON\r\n"
				+ "e.deptId = d.id;";
		List<EmpVo> list = q.queryRows(sql, EmpVo.class, null);
		for (EmpVo e : list) {
			System.out.println(e.getEname() + " " + e.getAnnualSal() + " " + e.getDeptName() + " " + e.getDeptAddr());
		}
	}
	static void test2() {
		long a = System.currentTimeMillis();
		for(int i=0;i<3000;i++) {
			Query q = QueryFactory.createQuery();
			List<Emp> list =q.queryRows("select ename, age,salary from emp where id > ?", Emp.class, new Object[]{1});
			for(Emp emp:list) {
				System.out.println(emp.getEname() +"  "+ emp.getAge()+" "+ emp.getSalary());
			}
		}
		long b = System.currentTimeMillis();
		System.out.println(b-a);
	}
}
