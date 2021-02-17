package com.digiwin.deploy.service;

import java.util.List;
import java.util.Map;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.DWService;

public interface IDeployService extends DWService {

	// 取得目前AppCode列表(單頭)
	public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

	// 取得目前AppCode列表(單身)
	public Object getDetailList(List<Object> oids) throws Exception;

	// 新增產品線佈署資訊
	public Object post(DWDataSet dataset) throws Exception;

	// 修改產品線佈署資訊
	public Object put(DWDataSet dataset) throws Exception;

	// 刪除產品線佈署
	public Object delete(List<Object> oids) throws Exception;

	// 移除佈署檔案
	public Object deleteDeployFile(DWDataSet dataset) throws Exception;

	// 發佈(指定)正式首佈版本
	public Object putIsPublish(String oid) throws Exception;

}
