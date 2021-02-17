package com.digiwin.deploy.service;

import java.util.Map;

import com.digiwin.app.service.DWService;

public interface ILocalSiteV01Service extends DWService {
	
	/**
	 * 取得地端列表
	 * 
	 * @param Map<String, Object> param
	 * @return
	 * @throws Exception
	 */
	public Object getList(Map<String, Object> param) throws Exception;
	
	/**
	 * 取得地端名稱列表
	 * 
	 * @param Map<String, Object> param
	 * @return
	 * @throws Exception
	 */
	public Object getSiteNames(Map<String, Object> param) throws Exception;

	/**
	 * 取得地端列表-雲平台使用
	 * 
	 * @param apName
	 * @return
	 * @throws Exception
	 */
	public Object getList(String apName) throws Exception;
	
	/**
	 * 取得地端列表-運維平台使用
	 * 
	 * @param apName
	 * @return
	 * @throws Exception
	 */
	public Object getList(String apName, String tid) throws Exception;

	/**
	 * 取得-地端健康狀態(cpu,memory,disk)
	 * 
	 * @param Map<String, Object> param
	 * @return
	 * @throws Exception
	 */
	public Object getAppPerformance(Map<String, Object> param) throws Exception;
	
	/**
	 * 取得-地端版本
	 * 
	 * @param Map<String, Object> param
	 * @return
	 * @throws Exception
	 */
	public Object getAppVersion(Map<String, Object> param) throws Exception;
	
	/**
	 * 取得-地端安裝紀錄
	 * 
	 * @param Map<String, Object> param
	 * @return
	 * @throws Exception
	 */
	public Object getAppInstallHistory(Map<String, Object> param) throws Exception;
	
	/**
	 * 取得-地端詳情
	 * 
	 * @param Map<String, Object> param
	 * @return
	 * @throws Exception
	 */
	public Object getDetail(Map<String, Object> param) throws Exception;
	
}
