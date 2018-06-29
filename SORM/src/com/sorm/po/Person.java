package com.sorm.po;
import java.sql.*;
import java.util.*;
public class Person {

	private Integer sex;
	private String name;
	private String telephone;
	private Integer id;
	private String pwd;
	private Integer age;
	private String email;
	private Double sal;


	public Person () {}


	public Integer getSex(){
		return sex;
	}
	public String getName(){
		return name;
	}
	public String getTelephone(){
		return telephone;
	}
	public Integer getId(){
		return id;
	}
	public String getPwd(){
		return pwd;
	}
	public Integer getAge(){
		return age;
	}
	public String getEmail(){
		return email;
	}
	public Double getSal(){
		return sal;
	}


	public void setSex(Integer sex){
		this.sex = sex;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setTelephone(String telephone){
		this.telephone = telephone;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public void setPwd(String pwd){
		this.pwd = pwd;
	}
	public void setAge(Integer age){
		this.age = age;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public void setSal(Double sal){
		this.sal = sal;
	}


}
