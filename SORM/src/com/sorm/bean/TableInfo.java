package com.sorm.bean;

import java.util.List;
import java.util.Map;

/**
 * 存储表结构的信息
 * @author Administrator
 *
 */
public class TableInfo {
	private String tname;
	private Map<String,ColumnInfo> columns;
	/**
	 * 唯一主键（目前只能处理有且仅有一个主键）
	 */
	private ColumnInfo onlyPriKey;
	
	private List<ColumnInfo> priKeys;
	
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public Map<String, ColumnInfo> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, ColumnInfo> columns) {
		this.columns = columns;
	}
	public ColumnInfo getOnlyPriKey() {
		return onlyPriKey;
	}
	public void setOnlyPriKey(ColumnInfo onlyPriKey) {
		this.onlyPriKey = onlyPriKey;
	}
	public TableInfo() {
		super();
	}
	public List<ColumnInfo> getPriKeys() {
		return priKeys;
	}
	public void setPriKeys(List<ColumnInfo> priKeys) {
		this.priKeys = priKeys;
	}
	public TableInfo(String tname, Map<String, ColumnInfo> columns, ColumnInfo onlyPriKey, List<ColumnInfo> priKeys) {
		super();
		this.tname = tname;
		this.columns = columns;
		this.onlyPriKey = onlyPriKey;
		this.priKeys = priKeys;
	}
	public TableInfo(String tname, Map<String, ColumnInfo> columns, List<ColumnInfo> priKeys) {
		super();
		this.tname = tname;
		this.columns = columns;
		this.priKeys = priKeys;
	}
	
	
	
}
