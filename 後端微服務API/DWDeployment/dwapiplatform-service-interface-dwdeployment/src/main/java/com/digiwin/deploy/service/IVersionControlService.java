package com.digiwin.deploy.service;

import java.util.Map;

import com.digiwin.app.service.DWService;

/**
 * 2019-6-6 版本控制服務接口
 * @author falcon
 *
 */
public interface IVersionControlService extends DWService {

	/**
	 * 取得應用的版本
	 * @return 應用的版本信息
	 * @throws Exception 異常
	 */
	public Object getApplication() throws Exception;
	
	/**
	 * 取得平台的版本 
	 * @return 平台的版本信息
	 * @throws Exception 異常
	 */
	public Object getPlatform() throws Exception;
	
	/**
	 * 更新產品
	 * @return 更新結果
	 * @throws Exception 異常
	 */
	public Object updateProduct(String version, Map<String, Object> params) throws Exception;
}
