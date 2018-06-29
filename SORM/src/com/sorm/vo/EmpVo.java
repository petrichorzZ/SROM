package com.sorm.vo;

public class EmpVo {
	private Integer id;
	private String  ename;
	private Double  annualSal;
	private Integer age;
	private String deptName;
	private String deptAddr;
	public EmpVo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EmpVo(Integer id, String ename, Double annualSal, Integer age, String deptName, String deptAddr) {
		super();
		this.id = id;
		this.ename = ename;
		this.annualSal = annualSal;
		this.age = age;
		this.deptName = deptName;
		this.deptAddr = deptAddr;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public Double getAnnualSal() {
		return annualSal;
	}
	public void setAnnualSal(Double annualSal) {
		this.annualSal = annualSal;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptAddr() {
		return deptAddr;
	}
	public void setDeptAddr(String deptAddr) {
		this.deptAddr = deptAddr;
	}
	
	
}
