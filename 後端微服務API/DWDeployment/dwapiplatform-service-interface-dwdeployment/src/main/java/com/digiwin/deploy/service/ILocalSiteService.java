package com.digiwin.deploy.service;

import java.util.Map;

import com.digiwin.app.service.DWService;

public interface ILocalSiteService extends DWService {

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

	/**
	 * 更新地端
	 * @param parameter
	 * @param param
	 * @return
	 * @throws Exception
	 */

	public Object putUpdate(Map<String, Object> param, Map<String, String> parameter)throws Exception;

	/**
	 * 取得-更新紀錄
	 *
	 * @return
	 * @throws Exception
	 */
	public Object getAppUpdateLog(Map<String, Object> param) throws Exception;

	/**
	 * 取得-單次安裝紀錄
	 *
	 * @return
	 * @throws Exception
	 */
	public Object getAppAssignLog(Map<String, Object> param,String version) throws Exception ;

	/**
	 * 取得安裝紀錄
	 *
	 * @return
	 * @throws Exception
	 */
	public Object getAppInstallLog(Map<String, Object> param) throws Exception;

//	/**
//	 * 取得-更新狀態
//	 *
//	 * @return
//	 * @throws Exception
//	 */
//	public Object getAppUpdateStatus(Map<String, Object> param) throws Exception;

	/**
	 * 比較版本
	 * @return
	 * @throws Exception
	 */
	public boolean getCompareVersion(Map<String, Object> param) throws Exception;
}
