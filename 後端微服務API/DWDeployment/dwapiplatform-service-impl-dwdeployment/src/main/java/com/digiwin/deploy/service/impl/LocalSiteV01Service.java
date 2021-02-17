package com.digiwin.deploy.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.digiwin.app.container.DWContainerContext;
import com.digiwin.app.container.DWDefaultParameters;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.persistconn.TenantIdProvider;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.serviceclient.SaasAdminServiceClient;
import com.digiwin.app.serviceclient.ServiceClient;
import com.digiwin.deploy.service.ILocalSiteV01Service;
import com.digiwin.utils.DWTenantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalSiteV01Service implements ILocalSiteV01Service {
	
	private static final String _appIdParam = "appId";
	private static final String _tidParam = "tenantId";
	private static final String _gatewayIdParam = "gId";
	private static final String _currentPageParam = "currentPage";
	private static final String _pageSizeParam = "pageSize";
	private static final String _localSideDeployArea = "p";
	
	private static final String _rtnLocalSitesKey = "localSites";
	private static final String _rtnInstallHistroyKey = "installHistory";
	private static final String _rtnRowCountKey = "rowCount";
	private static final String _rtnPageCountKey = "pageCount";	
	private static final String _rtnlocalSiteNamesKey = "localSiteNames";

	@Override
	public Object getList(Map<String, Object> param) throws Exception {
		String appId = "", tenantId = "";
		boolean isPaging = false;
		int pageSize=10,currentPage=1;
		
		if(param.containsKey(_appIdParam)) {
			appId = (String) param.get(_appIdParam);
		}
		
		if(param.containsKey(_tidParam)) {
			tenantId = (String) param.get(_tidParam);
		}
		
		if(param.containsKey(_currentPageParam)) {
			isPaging = true;
			currentPage = ((Double) param.getOrDefault(_currentPageParam,1)).intValue();
			pageSize = ((Double) param.getOrDefault(_pageSizeParam,10)).intValue();
		}
				
		if(StringUtils.isNotBlank(tenantId)) {
			Map<String, Object> profile = DWServiceContext.getContext().getProfile();
		    profile.put(DWTenantUtils.getIamTenantIdKey(),tenantId);
		}
		
		List<Map<String,Object>> localSites = new ArrayList<Map<String,Object>>();
		localSites = (List<Map<String, Object>>) getLocalSites(param).get(_rtnLocalSitesKey);
		
		Object result = null;
		int rowCount = localSites.size();
		
		if(isPaging && rowCount > 0) {
			result = getCurrentPageData(localSites, currentPage, pageSize);
		}else {
			Map<String, Object> dataMap =  new HashMap<String, Object>();
			dataMap.put(_rtnRowCountKey, rowCount);
			dataMap.put(_rtnLocalSitesKey, localSites);
			
			result = dataMap;
		}

		return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

	}

	@Override
	public Object getList(String apName, String tid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getList(String apName) throws Exception {
		String result = new SaasAdminServiceClient().queryRegister(apName);
		return DWServiceResultBuilder.build(result);
	}

	@Override
	public Object getAppPerformance(Map<String, Object> param) throws Exception {
		if(param == null || param.size() == 0) {
	        return DWServiceResultBuilder.build(false, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), "");
		}
		
		if(param.containsKey(_tidParam)) {
			String tid = (String) param.get(_tidParam);
			TenantIdProvider.setTenantId(tid);
		}

		String apName = (String) param.get(_appIdParam);
		
		ServiceClient sc = new ServiceClient();
		String responseLocalSite = sc.invokeTenantDapService((String)param.get(_gatewayIdParam), apName, "DWSys", "ISystemInfoService", "getPerformance", new HashMap());
		JSONObject responseJObj = new JSONObject(responseLocalSite);
		//以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
		String resultResponse = (String) responseJObj.getJSONArray("detail").getJSONObject(0).get("response");
		JSONObject performanceData =  new JSONObject(resultResponse).getJSONObject("response").getJSONObject("data");
		Map<String, Object> result = new HashMap<String, Object>();
		result = new ObjectMapper().readValue(performanceData.toString(), HashMap.class);
		return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

	}

	@Override
	public Object getAppInstallHistory(Map<String, Object> param) throws Exception {
		Object result = getLocalSiteInstallHistory(param);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
	}

	@Override
	public Object getAppVersion(Map<String, Object> param) throws Exception {
		if(param == null || param.size() == 0) {
	        return DWServiceResultBuilder.build(false, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), "");
		}

		String result = getLocalSiteVersion(param);
		
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
	}
	
	private String getLocalSiteVersion(Map<String, Object> param) throws Exception {
		
		if(param.containsKey(_tidParam)) {
			String tid = (String) param.get(_tidParam);
			TenantIdProvider.setTenantId(tid);
		}

		String apName = (String) param.get(_appIdParam);
		ServiceClient sc = new ServiceClient();
		String responseLocalSite = sc.invokeTenantDapService((String)param.get(_gatewayIdParam), apName, "DWSys", "ISystemInfoService", "getVersion", new HashMap());

		JSONObject responseJObj = new JSONObject(responseLocalSite);
		//以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
		String resultResponse = (String) responseJObj.getJSONArray("detail").getJSONObject(0).get("response");
		String result = new JSONObject(resultResponse).getJSONObject("response").getString("data");
		return result;
	}

	private Object getLocalSiteInstallHistory(Map<String, Object> param) throws Exception {
		
		if(param.containsKey(_tidParam)) {
			String tid = (String) param.get(_tidParam);
			TenantIdProvider.setTenantId(tid);
		}
		String apName = (String) param.get(_appIdParam);
		ServiceClient sc = new ServiceClient();
		String responseLocalSite = sc.invokeTenantDapService((String)param.get(_gatewayIdParam), apName, "DWSys", "ISystemInfoService", "getInstallHistory", new HashMap());

		JSONObject responseJObj = new JSONObject(responseLocalSite);
		//以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
		String resultResponse = (String) responseJObj.getJSONArray("detail").getJSONObject(0).get("response");
		String resultData = (String) new JSONObject(resultResponse).getJSONObject("response").get("data");
		JSONArray historyJA	= new JSONObject(resultData).getJSONArray(_rtnInstallHistroyKey);
		
		Map<String,Object> installRow = new HashMap<String, Object>();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		JSONObject jo;
        for (int i = 0; i <historyJA.length() ; i++) {
        	installRow = new HashMap<String, Object>();
            jo =historyJA.getJSONObject(i);
            installRow.put("deploy_version", jo.optString("deploy_version", ""));
            installRow.put("install_date", jo.optString("install_date", ""));
            result.add(installRow);
        }
        
		return result;
	}

	
	/**
	 * 取得最新可更新版本
	 * 
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	private String getAvailableUpdate(String appId) throws Exception {
		String result="";
    	DWDefaultParameters para = new DWDefaultParameters() ;
		para.put(_appIdParam, appId);
		para.put("deployArea", _localSideDeployArea);
		DWServiceResult response = (DWServiceResult)DWContainerContext.getInstance().invoke("DWDeployment", "IInstallService", "getAvailableUpdate", para, DWServiceContext.getContext().getProfile());
		if(response.getSuccess()) {
			DWDataSet data = (DWDataSet)response.getData();
			if(data.getTable(DBConstants.DEPLOY_INFO).getRows().size() > 0) {
				result = data.getTable(DBConstants.DEPLOY_INFO).getRow(0).get(DBConstants.DEPLOY_VERSION);
			}
		}
		return result;
	}
	
	/**
	 * 構造函數
	 * @param pageSize 一頁的資料筆數
	 * @param rowCount 總資料筆數
	 */
	private Object getCurrentPageData(Object data, int currentPage, int pageSize) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!(data instanceof List)) {
            result.put("error", "data type is not List");
            return result;
		}
		List dataList = (List) data;

        long rowCount = dataList.size();

		long pageCount = rowCount == 0 ? 0 : ((rowCount - 1) / pageSize) + 1;
		result.put(_rtnRowCountKey, rowCount);
        result.put(_rtnPageCountKey, pageCount);
        result.put(_pageSizeParam, pageSize);
        result.put(_currentPageParam, currentPage);
        
        if(currentPage > pageCount) {
            result.put("error", "currentPage > pageCount");
            return result;
        }
        
		int fromIndex = 0 ;
		int toIndex = pageSize;
		if(currentPage > 1) {
			fromIndex  = ((currentPage -1) * pageSize);
			toIndex = fromIndex + pageSize;
		}
		
		if(toIndex > rowCount) {
		   toIndex = (int) rowCount;
		}
		
		List datas = dataList.subList(fromIndex, toIndex);
		result.put(_rtnLocalSitesKey,datas);
		
		return result;		
	}
	
	/**
	 * 取地端清單
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getLocalSites(Map<String, Object> param) throws Exception {
		String appId = "", tenantId = "";
		String targetGid = "";
		boolean isAssignGid = false;
		
		if(param.containsKey(_appIdParam)) {
			appId = (String) param.get(_appIdParam);
		}
		
		if(param.containsKey(_tidParam)) {
			tenantId = (String) param.get(_tidParam);
		}
				
		if(StringUtils.isNotBlank(tenantId)) {
			Map<String, Object> profile = DWServiceContext.getContext().getProfile();
		    profile.put(DWTenantUtils.getIamTenantIdKey(),tenantId);
		}
		
		if(param.containsKey(_gatewayIdParam)) {
			isAssignGid = true;
			targetGid = (String) param.get(_gatewayIdParam);
		}
		
		String localSitesStr = new SaasAdminServiceClient().queryRegister(appId);
		JSONArray localSideJA = new JSONArray(localSitesStr);
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> queryLS =  new HashMap<String, Object>();
		List<Map<String,Object>> localSites = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> localSiteNames = new ArrayList<Map<String,Object>>();
		Map<String, Object> localSiteMap =  new HashMap<String, Object>();
		//地端查詢
		Map<String, Object> localSiteNameMap =  new HashMap<String, Object>();
		String lsVerion;
		String aviableVerion;
		String gId,gName;
		JSONObject objson = new JSONObject();

		for(Object obj : localSideJA) {
			
			localSiteMap =  new HashMap<String, Object>();
			if(obj instanceof JSONObject) {
				
				objson = ((JSONObject) obj);
	
				retMap = new ObjectMapper().readValue(objson.toString(), HashMap.class);
				queryLS.put(_appIdParam, appId);//應用id
				localSiteMap.put(_appIdParam, appId);
				//地端id
				gId = (String) retMap.get("gatewayId");
				if(isAssignGid) {
					if(!gId.equals(targetGid)) {
						continue;
					}
				}
				
				queryLS.put(_gatewayIdParam, gId);
				localSiteMap.put(_gatewayIdParam, gId);
				//地端名稱
				gName = (String) retMap.get("gatewayName");
				queryLS.put("gName", gName);
				localSiteMap.put("gName", gName);
				
				//TODO 長連接1.0
				if(StringUtils.isNotBlank(tenantId)) {
					localSiteMap.put("tenantId", tenantId);
				}else {
					localSiteMap.put("tenantId", "tempTenantId");	
				}
				
				if(StringUtils.isNoneBlank(appId)) {
					localSiteMap.put(_appIdParam, appId);
				}else {
					localSiteMap.put(_appIdParam, "tempAppId");
				}
				
				localSiteMap.put("tenantName", "tempTenant名稱1");
				localSiteMap.put("appName", "temp應用名稱1");
				
				localSiteNameMap.put(gId, gName);
				//地端已安裝版本
				lsVerion = (String) getLocalSiteVersion(queryLS);
				localSiteMap.put("version", lsVerion);
				//地端可安裝版本
				aviableVerion = (String) getAvailableUpdate(appId);
				localSiteMap.put("newVerion", aviableVerion);
				//地端連線狀態,長連接 0.1 都是 true
				localSiteMap.put("connectStatus", "true");
				//地端安裝紀錄
				//lsInstallHistoryData = (List) getLocalSiteInstallHistory(queryLS);
				//lsInstallHistory = new JSONObject(lsInstallHistoryStr);
				localSiteMap.put(_rtnInstallHistroyKey, getLocalSiteInstallHistory(queryLS));
				localSites.add(localSiteMap);
				localSiteNames.add(localSiteNameMap);
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(_rtnLocalSitesKey, localSites);
		result.put("localSiteNames", localSiteNames);
		
		return result;
	}

	@Override
	public Object getSiteNames(Map<String, Object> param) throws Exception {
		Map<String, Object> localSites = getLocalSites(param);
		Object result = localSites.get(_rtnlocalSiteNamesKey);
		return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
	}

	@Override
	public Object getDetail(Map<String, Object> param) throws Exception {
		String appId = "", tenantId = "";
		
		if(param.containsKey(_appIdParam)) {
			appId = (String) param.get(_appIdParam);
		}
		
		if(param.containsKey(_tidParam)) {
			tenantId = (String) param.get(_tidParam);
		}
				
		if(StringUtils.isNotBlank(tenantId)) {
			Map<String, Object> profile = DWServiceContext.getContext().getProfile();
		    profile.put(DWTenantUtils.getIamTenantIdKey(),tenantId);
		}
		
		String localSitesStr = new SaasAdminServiceClient().queryRegister(appId);
		JSONArray localSideJA = new JSONArray(localSitesStr);
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> queryLS =  new HashMap<String, Object>();
		List<Map<String,Object>> localSites = new ArrayList<Map<String,Object>>();

		localSites = (List<Map<String, Object>>) getLocalSites(param).get(_rtnLocalSitesKey);
		Object result = localSites;

		return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

	}
	
}
