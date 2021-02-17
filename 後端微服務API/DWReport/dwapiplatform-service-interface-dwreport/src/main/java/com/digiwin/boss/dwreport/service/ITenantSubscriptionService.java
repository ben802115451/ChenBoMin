package com.digiwin.boss.dwreport.service;

import java.util.Map;

import com.digiwin.app.service.DWService;

public interface ITenantSubscriptionService extends DWService {

	public Object get(int pageNum, int pageSize, Map<String, Object> params) throws Exception;
	
	public Object getFile(Map<String, Object> params) throws Exception;
}
