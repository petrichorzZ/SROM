package com.sorm.po;
import java.sql.*;
import java.util.*;
public class User {

	private String address;
	private String name;
	private Integer id;
	private String pwd;
	private java.sql.Date hiredate;
	private Double sal;


	public User () {}


	public String getAddress(){
		return address;
	}
	public String getName(){
		return name;
	}
	public Integer getId(){
		return id;
	}
	public String getPwd(){
		return pwd;
	}
	public java.sql.Date getHiredate(){
		return hiredate;
	}
	public Double getSal(){
		return sal;
	}


	public void setAddress(String address){
		this.address = address;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public void setPwd(String pwd){
		this.pwd = pwd;
	}
	public void setHiredate(java.sql.Date hiredate){
		this.hiredate = hiredate;
	}
	public void setSal(Double sal){
		this.sal = sal;
	}


}
