package com.digiwin.deploy.service;

import com.digiwin.app.service.DWService;

public interface IInstallService extends DWService {

	/**
	 * 取得正式發佈的最新版和相對應檔案ID
	 * @param appId 應用編號
	 * @param deployArea 佈署區域
	 * @return 應用所屬佈署區域的各端發佈的安裝文件信息
	 * @throws Exception 異常
	 */
	public Object getPublishReleased(String appId, String deployArea) throws Exception;

	// 取得指定發佈的版本和相對應檔案ID
	public Object getSpecificReleased(String appId, String deployArea, String deploySite, String version) throws Exception;

	// 取得[最新]的可更新的版本
	public Object getAvailableUpdate(String appId, String deployArea) throws Exception;
	
	// 向Repository取得image檔案列表
	public Object getImageNameList(String appId, String deploySite) throws Exception;
	
}
