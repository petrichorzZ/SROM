package com.sorm.po;
import java.sql.*;
import java.util.*;
public class Emp {

	private java.sql.Date birthday;
	private String ename;
	private Integer deptId;
	private Integer id;
	private Double salary;
	private Integer age;


	public Emp () {}


	public java.sql.Date getBirthday(){
		return birthday;
	}
	public String getEname(){
		return ename;
	}
	public Integer getDeptId(){
		return deptId;
	}
	public Integer getId(){
		return id;
	}
	public Double getSalary(){
		return salary;
	}
	public Integer getAge(){
		return age;
	}


	public void setBirthday(java.sql.Date birthday){
		this.birthday = birthday;
	}
	public void setEname(String ename){
		this.ename = ename;
	}
	public void setDeptId(Integer deptId){
		this.deptId = deptId;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public void setSalary(Double salary){
		this.salary = salary;
	}
	public void setAge(Integer age){
		this.age = age;
	}


}
