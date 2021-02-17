package com.digiwin.boss.dwreport.dao;

import java.util.ArrayList;
import java.util.List;

public class SqlWhereCondition {

	private StringBuilder sql;
	
	private List<Object> sqlParams;

	public SqlWhereCondition() {
		sql = new StringBuilder();
		sqlParams = new ArrayList<Object>();
	}

	public void appenSql(String sql) {
		this.sql.append(sql);
	}
	
	public void addSqlParams(Object value) {
		sqlParams.add(value);
	}
		
	public String getSql() {
		return sql.toString();
	}

	public void setSql(String sql) {
		this.sql.append(sql);
	}

	public List<Object> getSqlParams() {
		return sqlParams;
	}

	public void setSqlParams(List<Object> sqlParams) {
		this.sqlParams = sqlParams;
	}

}
