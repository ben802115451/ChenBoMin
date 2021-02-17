package com.digiwin.deploy.service.impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.json.gson.DWGsonProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.digiwin.deploy.service.ILocalSiteService;
import com.digiwin.utils.DWTenantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocalSiteService implements ILocalSiteService {

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    private static final Log _log = LogFactory.getLog(LocalSiteService.class);

    private static final String _appIdParam = "appId";
    private static final String _tenantIdParam = "tenantId";
    private static final String _gatewayIdParam = "gId";
    private static final String _currentPageParam = "currentPage";
    private static final String _pageSizeParam = "pageSize";
    private static final String _localSideDeployArea = "p";

    private static final String _lsGatewayId = "gatewayId";
    private static final String _lsGatewayName = "gatewayName";
    private static final String _lsAppId = "apName"; //長連接回傳的 tag:appName,其實是 應用Id
    private static final String _lsAppName = "appName"; //未實現
    private static final String _lsTenantName = "tenantName"; //未實現
    private static final String _lsIsOnline = "isOnline";

    private static final String _rtnLocalSitesKey = "localSites";
    private static final String _rtnInstallHistroyKey = "installHistory";
    private static final String _rtnRowCountKey = "rowCount";
    private static final String _rtnPageCountKey = "pageCount";
    private static final String _rtnlocalSiteNamesKey = "localSiteNames";
    private static final String _rtnGatewayNameKey = "gName";
    private static final String _rtnConnectStatus = "connectStatus";

    private static final String _rtnErrorJsonKey = "issuejson";
    private static final String _version = "version";
    private static final String _TABLE = "update_detail";

    static final String INSERT_SQL = "INSERT INTO update_detail (gatewayId, apName, version, userId, userName) values(?, ?, ?, ?, ?)";
    static final String UPDATE_STATUS_SQL = "UPDATE update_detail SET STATUS = ?, version = ? WHERE gatewayId = ?";
    static final String DELETE_SQL = "DELETE FROM update_detail WHERE gatewayId = ?";
    static final String SELECT_SQL = "SELECT * FROM update_detail WHERE gatewayId = ?";

    @Override
    public Object getList(Map<String, Object> param) throws Exception {

        if (param == null || param.size() == 0) {
            return DWServiceResultBuilder.build(false,
                    DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg002", "msg002"), "");
        }

        String appId = "", tenantId = "";
        boolean isPaging = false;
        int pageSize = 10, currentPage = 1;

        if (param.containsKey(_appIdParam)) {
            appId = (String) param.get(_appIdParam);
        }

        if (param.containsKey(_tenantIdParam)) {
            tenantId = (String) param.get(_tenantIdParam);
        }

        if (param.containsKey(_currentPageParam)) {
            isPaging = true;
            currentPage = ((Double) param.getOrDefault(_currentPageParam, 1)).intValue();
            pageSize = ((Double) param.getOrDefault(_pageSizeParam, 10)).intValue();
        }

        /*
         * if(StringUtils.isNotBlank(tenantId)) { Map<String, Object> profile =
         * DWServiceContext.getContext().getProfile();
         * profile.put(DWTenantUtils.getIamTenantIdKey(),tenantId); }
         */
        List<Map<String, Object>> localSites = new ArrayList<Map<String, Object>>();
        Map<String, Object> localSitesOrg = getLocalSites(param);
        localSites = (List<Map<String, Object>>) localSitesOrg.get(_rtnLocalSitesKey);

        Object result = null;
        int rowCount = localSites.size();

        if (isPaging && rowCount > 0) {
            result = getCurrentPageData(localSites, currentPage, pageSize);
        } else {

            addLocalSiteInfo(localSites);

            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put(_rtnRowCountKey, rowCount);
            dataMap.put(_rtnLocalSitesKey, localSites);
            //排查問題
            if (localSitesOrg.containsKey(_rtnErrorJsonKey)) {
                dataMap.put(_rtnErrorJsonKey, localSitesOrg.get(_rtnErrorJsonKey));
            }
            result = dataMap;
        }

        return DWServiceResultBuilder.build(true,
                DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

    }

    @Override
    public Object getAppPerformance(Map<String, Object> param) throws Exception {
        if (param == null || param.size() == 0 || !(param.containsKey(_tenantIdParam))) {
            return DWServiceResultBuilder.build(false,
                    DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg002", "msg002"), "");
        }

        if (param.containsKey(_tenantIdParam)) {
            String tid = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tid);
        }

        String apName = (String) param.get(_appIdParam);

        ServiceClient sc = new ServiceClient();
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getPerformance", new HashMap(),
                10, TimeUnit.SECONDS);

        JSONObject responseJObj = new JSONObject(responseLocalSite);
        //以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
        //String resultResponse = (String) responseJObj.getJSONArray("detail").getJSONObject(0).get("response");
        JSONArray responseDetail = responseJObj.getJSONArray("detail");
        responseJObj = (JSONObject) responseDetail.get(0);
        boolean resultResponse = (boolean) responseJObj.optBoolean("result", false);
        Map<String, Object> result = new HashMap<String, Object>();
        if (resultResponse) {
            String response = (String) responseJObj.get("response");
            JSONObject responseJO = new JSONObject(response);
            int responseStatus = responseJO.getInt("status");
            if (responseStatus == 200) {
                JSONObject performanceData = responseJO.getJSONObject("response").getJSONObject("data");
                result = new ObjectMapper().readValue(performanceData.toString(), HashMap.class);
            } else {
                _log.debug("[getAppPerformance] ServiceClient response status !=200");
            }
        }

        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

    }

    @Override
    public Object getAppInstallHistory(Map<String, Object> param) throws Exception {
        Object result = getLocalSiteInstallHistory(param);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
    }

    @Override
    public Object getAppVersion(Map<String, Object> param) throws Exception {
        if (param == null || param.size() == 0) {
            return DWServiceResultBuilder.build(false, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), "");
        }

        Map<String, Object> versionMap = (Map<String, Object>) getLocalSiteVersion(param);

        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), versionMap);
    }

    private Map<String, Object> getLocalSiteVersion(Map<String, Object> param) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> parameter = new HashMap<String, Object>();
        Map<String, Object> profile = new HashMap<String, Object>();
        if (!param.containsKey(_gatewayIdParam)) {
            //return DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg002", "msg002");
            result.put("result", false);
            return result;
        }

        if (param.containsKey(_tenantIdParam)) {
            String tenantId = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tenantId);
            profile.put(_tenantIdParam, tenantId);
        }

        String apName = (String) param.get(_appIdParam);
        ServiceClient sc = new ServiceClient();
        parameter.put("profile", profile);
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getVersion", new HashMap(),
                10, TimeUnit.SECONDS);

        JSONObject responseJObj = new JSONObject(responseLocalSite);

        JSONArray responseDetail = responseJObj.getJSONArray("detail");
        responseJObj = (JSONObject) responseDetail.get(0);
        //以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
        boolean resultResponse = (boolean) responseJObj.optBoolean("result", false);

        result.put("result", resultResponse);

        if (resultResponse) {
            String response = (String) responseJObj.get("response");
            JSONObject responseJO = new JSONObject(response);
            int responseStatusCode = responseJO.getInt("status");
            String version = "";
            if (responseStatusCode == 200) {
                //String resultData = new JSONObject(response).getJSONObject("response").getString("data");
                version = new JSONObject(response).getJSONObject("response").getString("data");
            } else {
                _log.debug("[getLocalSiteVersion] ServiceClient response status !=200");
                result.put("result", false);
                result.put("status", responseStatusCode);
                result.put("debugInfo", responseJO.get("debugInfo"));
            }
            result.put("version", version);
        } else {
            result.put("version", "");
            _log.debug("[getLocalSiteVersion] ServiceClient response result=false");
        }

        return result;
    }

    private Object getLocalSiteInstallHistory(Map<String, Object> param) throws Exception {

        if (param.containsKey(_tenantIdParam)) {
            String tid = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tid);
        }
        String apName = (String) param.get(_appIdParam);
        ServiceClient sc = new ServiceClient();
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getInstallHistory", new HashMap(),
                10, TimeUnit.SECONDS);

        JSONObject responseJObj = new JSONObject(responseLocalSite);
        //以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
        JSONArray responseDetail = responseJObj.getJSONArray("detail");
        responseJObj = (JSONObject) responseDetail.get(0);

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        boolean resultResponse = (boolean) responseJObj.optBoolean("result", false);
        if (resultResponse) {
            String response = (String) responseJObj.get("response");
            JSONObject responseJO = new JSONObject(response);
            int responseStatusCode = responseJO.getInt("status");
            if (responseStatusCode == 200) {
                String installLog = new JSONObject(response).getJSONObject("response").getString("data");
                result = getInstallLog(installLog);
					/*
					JSONArray historyJA	= new JSONObject(resultData).getJSONArray(_rtnInstallHistroyKey);
					Map<String,Object> installRow = new HashMap<String, Object>();

					JSONObject jo;
			        for (int i = 0; i <historyJA.length() ; i++) {
			        	installRow = new HashMap<String, Object>();
			            jo =historyJA.getJSONObject(i);
			            installRow.put("deploy_version", jo.optString("deploy_version", ""));
			            installRow.put("install_date", jo.optString("install_date", ""));
			            result.add(installRow);
			        }
			        */
                //}
            } else {
                _log.debug("[getLocalSiteInstallHistory] ServiceClient response status !=200");
            }
        } else {
            _log.debug("[getLocalSiteInstallHistory] ServiceClient response result=false");
        }

        return result;
    }


    private Map<String, Object> getLocalSiteDeployInfo(Map<String, Object> param) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        if (!param.containsKey(_gatewayIdParam)) {
            //return DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg002", "msg002");
            result.put("result", false);
            return result;
        }

        if (param.containsKey(_tenantIdParam)) {
            TenantIdProvider.setTenantId((String) param.get(_tenantIdParam));
        }

        String apName = (String) param.get(_appIdParam);
        ServiceClient sc = new ServiceClient();
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getDeployInformation", new HashMap(),
                10, TimeUnit.SECONDS);

        JSONObject responseJObj = new JSONObject(responseLocalSite);
        //以下這兩行,若用一行,會報錯 org.json.JSONException: JSONObject["response"] is not a JSONObject
        JSONArray responseDetail = responseJObj.getJSONArray("detail");
        responseJObj = (JSONObject) responseDetail.get(0);

        boolean resultResponse = (boolean) responseJObj.optBoolean("result", false);

        if (resultResponse) {
            String response = (String) responseJObj.get("response");
            JSONObject responseJO = new JSONObject(response);
            int responseStatusCode = responseJO.getInt("status");
            if (responseStatusCode == 200) {//responseStatus=400
                Map<String, Object> installInfoMap = new HashMap<String, Object>();
                installInfoMap = new ObjectMapper().readValue(responseJO.getJSONObject("response").toString(), HashMap.class);
                result.put("version", installInfoMap.get("version"));
                //Map<String,Object> installHistoryMap = new HashMap<String, Object>();

                String installLog = (String) installInfoMap.get(_rtnInstallHistroyKey);
                List<Map<String, Object>> installHistory = getInstallLog(installLog);
                result.put(_rtnInstallHistroyKey, installHistory);
            } else {
                _log.debug("[getLocalSiteDeployInfo] ServiceClient response status !=200");
                _log.debug(String.format("  status code %s", responseStatusCode));
                _log.debug(String.format("  debugInfo %s ", responseJO.optString("debugInfo", "")));
            }
        } else {
            _log.debug("[getLocalSiteDeployInfo] ServiceClient response result=false");
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
        String result = "";
        DWDefaultParameters para = new DWDefaultParameters();
        para.put(_appIdParam, appId);
        para.put("deployArea", _localSideDeployArea);
        DWServiceResult response = (DWServiceResult) DWContainerContext.getInstance().invoke(
                "DWDeployment", "IInstallService", "getAvailableUpdate", para,
                DWServiceContext.getContext().getProfile());
        if (response.getSuccess()) {
            DWDataSet data = (DWDataSet) response.getData();
            if (data.getTable(com.digiwin.deploy.service.impl.DBConstants.DEPLOY_INFO).getRows().size() > 0) {
                result = data.getTable(com.digiwin.deploy.service.impl.DBConstants.DEPLOY_INFO).getRow(0).get(DBConstants.DEPLOY_VERSION);
            }
        }
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
        Map<String, Object> result = new HashMap<String, Object>();
        String appId = "", tenantId = "", queryConnectStatus = "";
        String targetGid = "";

        boolean isAssignGid = false;
        boolean isAssignConnectStatus = false;

        if (param.containsKey(_appIdParam)) {
            appId = (String) param.get(_appIdParam);
        }

        if (param.containsKey(_tenantIdParam)) {
            tenantId = (String) param.get(_tenantIdParam);
        }

        if (param.containsKey(_rtnConnectStatus)) {
            isAssignConnectStatus = true;
            queryConnectStatus = (String) param.get(_rtnConnectStatus);
        }

        if (StringUtils.isNotBlank(tenantId)) {
            Map<String, Object> profile = DWServiceContext.getContext().getProfile();
            profile.put(DWTenantUtils.getIamTenantIdKey(), tenantId);
        }

        /*
         * if(StringUtils.isNotBlank(tenantId)) { Map<String, Object> profile =
         * DWServiceContext.getContext().getProfile();
         * profile.put(DWTenantUtils.getIamTenantIdKey(),tenantId);
         * if(param.containsKey(_tenantIdParam)) { TenantIdProvider.setTenantId((String)
         * param.get(_tenantIdParam)); } }
         */
        if (param.containsKey(_gatewayIdParam)) {
            isAssignGid = true;
            targetGid = (String) param.get(_gatewayIdParam);
        }

        SaasAdminServiceClient sasc = new SaasAdminServiceClient();
        //String localSitesStr = sasc.queryRegister(appId);
        String localSitesStr = "[]";

        if (StringUtils.isNotBlank(tenantId)) {
            if (StringUtils.isNotBlank(appId)) {
                localSitesStr = sasc.queryRegister(tenantId, appId, true);
            } else {
                localSitesStr = sasc.queryRegisterUnderTenantId(tenantId, true);
            }
        } else {
            if (StringUtils.isNotBlank(appId)) {
                localSitesStr = sasc.queryRegisterUnderApName(appId, true);
            }//else {
            //都沒有指定條件
            //localSitesStr = "";
            //}
        }
        JSONArray localSideJA;
        try {
            localSideJA = new JSONArray(localSitesStr);
        } catch (Exception e) {
            _log.debug(_rtnErrorJsonKey + " : " + localSitesStr, e);
            result.put(_rtnErrorJsonKey, localSitesStr);
            localSideJA = new JSONArray();
        }

        Map<String, Object> retMap = new HashMap<String, Object>();

        List<Map<String, Object>> localSites = new ArrayList<Map<String, Object>>();
        Map<String, Object> localSiteRow = new HashMap<String, Object>();
        //地端名稱下拉清單
        List<Map<String, Object>> localSiteNames = new ArrayList<Map<String, Object>>();
        Map<String, Object> localSiteNameRow = new HashMap<String, Object>();
        String lsGatewayId, lsGatewayName, lsTenantId, lsTenantName, lsAppId, lsAppName, lsConnectStatus;
        JSONObject objson = new JSONObject();

        for (Object obj : localSideJA) {
            localSiteRow = new HashMap<String, Object>();
            if (obj instanceof JSONObject) {

                localSiteNameRow = new HashMap<String, Object>();

                objson = ((JSONObject) obj);
                retMap = (Map<String, Object>) new ObjectMapper().readValue(objson.toString(), HashMap.class);

                //地端資訊

                //地端資訊-地端id
                lsGatewayId = (String) retMap.getOrDefault(_lsGatewayId, "");
                //地端-地端Name
                lsGatewayName = (String) retMap.getOrDefault(_lsGatewayName, "");

                //{ "7c698dc266baa734470c66bd4830fa", "MyGatewayTest1" }
                localSiteNameRow.put(lsGatewayId, lsGatewayName);
                localSiteNames.add(localSiteNameRow);

                if (isAssignGid) {
                    //若有指定搜尋特定gId
                    if (!lsGatewayId.equals(targetGid)) {
                        //不符合,就找下一筆
                        continue;
                    }
                }
                lsAppId = (String) retMap.getOrDefault(_lsAppId, "");

                //地端連線狀態,長連接 0.1 都是 true
                lsConnectStatus = (String) retMap.getOrDefault(_lsIsOnline, true).toString();

                if (lsConnectStatus == "true") {
                    lsConnectStatus = this.getConnectStatus(param, lsGatewayId, lsAppId);
                }

                if (isAssignConnectStatus) {
                    //若有指定搜尋特定連線狀態
                    if (!lsConnectStatus.equalsIgnoreCase(queryConnectStatus)) {
                        //不符合,就找下一筆
                        continue;
                    }
                }

                localSiteRow.put(_gatewayIdParam, lsGatewayId);
                localSiteRow.put(_rtnGatewayNameKey, lsGatewayName);
                localSiteRow.put(_rtnConnectStatus, lsConnectStatus);

                //應用id,長連接回傳的 tag:appName,其實是 應用Id
                localSiteRow.put(_appIdParam, lsAppId);

                //地端-應用名稱
                lsAppName = (String) retMap.getOrDefault(_lsAppName, "temp應用名稱1");
                localSiteRow.put("appName", lsAppName);    //TODO 要換成變數

                //長連接1.0
                //地端-租戶Id
                lsTenantId = (String) retMap.getOrDefault(_tenantIdParam, tenantId);
                localSiteRow.put(_tenantIdParam, lsTenantId);

                //地端資訊-租戶名稱
                lsTenantName = (String) retMap.getOrDefault(_lsTenantName, "tempTenant名稱1");
                localSiteRow.put(_lsTenantName, lsTenantName);

                localSites.add(localSiteRow);
            }
        }

        result.put(_rtnLocalSitesKey, localSites);
        result.put(_rtnlocalSiteNamesKey, localSiteNames);

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

        if (param.containsKey(_appIdParam)) {
            appId = (String) param.get(_appIdParam);
        }

        if (param.containsKey(_tenantIdParam)) {
            tenantId = (String) param.get(_tenantIdParam);
        }

        if (StringUtils.isNotBlank(tenantId)) {
            Map<String, Object> profile = DWServiceContext.getContext().getProfile();
            profile.put(DWTenantUtils.getIamTenantIdKey(), tenantId);
        }

        //String localSitesStr = new SaasAdminServiceClient().queryRegister(appId);
        //JSONArray localSideJA = new JSONArray(localSitesStr);
        //Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map<String, Object>> localSites = new ArrayList<Map<String, Object>>();

        localSites = (List<Map<String, Object>>) getLocalSites(param).get(_rtnLocalSitesKey);
        addLocalSiteInfo(localSites);

        Object result = localSites;

        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

    }

    /**
     * 實際呼叫服務,取得相關資訊
     * 地端-版本
     * BOSS-新版本
     * 地端-安裝紀錄
     *
     * @param datas
     * @throws Exception
     */
    private void addLocalSiteInfo(List datas) throws Exception {

        String lsGatewayId, lsGatewayName, lsAppId, lsTenantId;
        String aviableVerion, lsVersion;
        Map<String, Object> queryLS = new HashMap<String, Object>();
        Map<String, Object> versionMap = new HashMap<String, Object>();
        Map<String, Object> deployInfoMap = new HashMap<String, Object>();

        for (Map<String, Object> localSiteRow : (List<Map<String, Object>>) datas) {

            if (!(localSiteRow.containsKey(_rtnConnectStatus) && localSiteRow.get(_rtnConnectStatus).equals("true"))) {
                //離線的地端,無法呼叫地端服務
                continue;
            }

            queryLS.clear();
            versionMap.clear();
            lsGatewayId = (String) localSiteRow.get(_gatewayIdParam);
            lsAppId = (String) localSiteRow.get(_appIdParam);
            lsGatewayName = (String) localSiteRow.get(_rtnGatewayNameKey);
            lsTenantId = (String) localSiteRow.get(_tenantIdParam);

            queryLS.put(_gatewayIdParam, lsGatewayId);
            queryLS.put(_appIdParam, lsAppId);
            queryLS.put(_rtnGatewayNameKey, lsGatewayName);
            queryLS.put(_tenantIdParam, lsTenantId);

            //地端已安裝紀錄,只調用一次
            deployInfoMap = (Map<String, Object>) getLocalSiteDeployInfo(queryLS);
            localSiteRow.putAll(deployInfoMap);

            //地端已安裝版本
            //versionMap = (Map<String, Object>) getLocalSiteVersion(queryLS);
            //lsVersion = (String) versionMap.getOrDefault("version", "");
            //localSiteRow.put("version", lsVersion);
            //地端安裝紀錄
            //localSiteRow.put(_rtnInstallHistroyKey, getLocalSiteInstallHistory(queryLS));

            //地端可升級安裝版本
            aviableVerion = (String) getAvailableUpdate(lsAppId);
            localSiteRow.put("newVersion", aviableVerion);
        }
    }

    /**
     * 構造函數
     *
     * @param pageSize    一頁的資料筆數
     * @param currentPage 總資料筆數
     * @throws Exception
     */
    private Object getCurrentPageData(Object data, int currentPage, int pageSize) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (!(data instanceof List)) {
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

        if (currentPage > pageCount) {
            result.put("error", "currentPage > pageCount");
            return result;
        }

        int fromIndex = 0;
        int toIndex = pageSize;
        if (currentPage > 1) {
            fromIndex = ((currentPage - 1) * pageSize);
            toIndex = fromIndex + pageSize;
        }

        if (toIndex > rowCount) {
            toIndex = (int) rowCount;
        }

        List<Map<String, Object>> datas = dataList.subList(fromIndex, toIndex);
        addLocalSiteInfo(datas);
        result.put(_rtnLocalSitesKey, datas);

        return result;
    }

    private List<Map<String, Object>> getInstallLog(String serviceResponseData) {
        JSONArray historyJA = new JSONObject(serviceResponseData).getJSONArray(_rtnInstallHistroyKey);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> installRow = new HashMap<String, Object>();
        JSONObject jo;
        for (int i = 0; i < historyJA.length(); i++) {
            installRow = new HashMap<String, Object>();
            jo = historyJA.getJSONObject(i);
            installRow.put("deploy_version", jo.optString("deploy_version", ""));
            installRow.put("install_date", jo.optString("install_date", ""));
            result.add(installRow);
        }
        return result;
    }

    /**
     * 更新地端
     *
     * @param parameter
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public Object putUpdate(Map<String, Object> param, Map<String, String> parameter) throws Exception {

        String result = new String();

        if (param.containsKey(_tenantIdParam)) {
            String tid = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tid);
        }

        String apName = new String();
        if (param.containsKey(_appIdParam)) {
            apName = (String) param.get(_appIdParam);
        }

        String gId = new String();
        if (param.containsKey(_gatewayIdParam)) {
            gId = (String) param.get(_gatewayIdParam);
        }

        String deploy_version = this.getAvailableUpdate(apName);

        Map<String, Object> profile = DWServiceContext.getContext().getProfile(); // 取得調用的"userId"以及"userName"
        String userId = (String) profile.get("userId");
        String userName = (String) profile.get("userName");

        Map<String, Object> params = new HashMap<>();
        params.put("params", parameter);
        params.put("profile", profile);

        ServiceClient sc = new ServiceClient();
        result = sc.invokeTenantDapService(gId, apName,
                "DWSys", "IOnPremisesUpdateService", "putUpdate", params,
                10, TimeUnit.SECONDS);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);


        dao.update(option, DELETE_SQL, gId);

        dao.update(option, INSERT_SQL, gId, apName, deploy_version, userId, userName);

        return result;
    }

    /**
     * 取得-更新紀錄
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getAppUpdateLog(Map<String, Object> param) throws Exception {

        if (param.containsKey(_tenantIdParam)) {
            String tid = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tid);
        }
        String apName = (String) param.get(_appIdParam);

        ServiceClient sc = new ServiceClient();
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getUpdateLog", new HashMap(),
                10, TimeUnit.SECONDS);

        String result = new String();

        Map<String, Object> invokeResultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(responseLocalSite, Map.class);
        List<Map<String, Object>> detail = (List<Map<String, Object>>) invokeResultMap.get("detail");
        Map<String, Object> detailMap = detail.get(0);
        if ((boolean) detailMap.get("result")) {

            String invokeResult = (String) detailMap.get("response");
            Map<String, Object> resultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(invokeResult, Map.class);

            if ((Double) resultMap.get("status") == 200.0) {

                Map<String, Object> response = (Map<String, Object>) resultMap.get("response");
                result = (String) response.get("data");

            } else {
                _log.debug("[getLocalSiteUpdateLog] ServiceClient response status !=200");
            }
        } else {
            _log.debug("[getLocalSiteUpdateLog] ServiceClient response result=false");
        }
        return DWServiceResultBuilder.build(true, result);
    }

    /**
     * 取得-單次安裝紀錄
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getAppAssignLog(Map<String, Object> param, String version) throws Exception {

        if (param.containsKey(_tenantIdParam)) {
            String tid = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tid);
        }
        String apName = (String) param.get(_appIdParam);

        Map<String, Object> versionMap = new HashMap<>();
        versionMap.put("version", version);

        ServiceClient sc = new ServiceClient();
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getInstallLog", versionMap,
                10, TimeUnit.SECONDS);

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> invokeResultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(responseLocalSite, Map.class);
        List<Map<String, Object>> detail = (List<Map<String, Object>>) invokeResultMap.get("detail");
        Map<String, Object> detailMap = detail.get(0);

        if ((boolean) detailMap.get("result")) {

            String invokeResult = (String) detailMap.get("response");
            Map<String, Object> resultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(invokeResult, Map.class);

            if ((Double) resultMap.get("status") == 200.0) {

                Map<String, Object> response = (Map<String, Object>) resultMap.get("response");
                if ((boolean) response.get("success")) {
                    result = (Map<String, Object>) response.get("data");
                } else {
                    return DWServiceResultBuilder.build(false, response.get("data"));
                }
            } else {
                _log.debug("[getLocalSiteAssignLog] ServiceClient response status !=200");
            }
        } else {
            _log.debug("[getLocalSiteAssignLog] ServiceClient response result=false");
        }
        return DWServiceResultBuilder.build(true, result);
    }

    /**
     * 取得安裝紀錄
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getAppInstallLog(Map<String, Object> param) throws Exception {

        if (param.containsKey(_tenantIdParam)) {
            String tid = (String) param.get(_tenantIdParam);
            TenantIdProvider.setTenantId(tid);
        }
        String apName = (String) param.get(_appIdParam);

        ServiceClient sc = new ServiceClient();
        String responseLocalSite = sc.invokeTenantDapService((String) param.get(_gatewayIdParam), apName,
                "DWSys", "ISystemInfoService", "getInstallHistory", new HashMap<>(),
                10, TimeUnit.SECONDS);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> installHistory = new ArrayList<>();

        Map<String, Object> invokeResultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(responseLocalSite, Map.class);
        List<Map<String, Object>> detail = (List<Map<String, Object>>) invokeResultMap.get("detail");
        Map<String, Object> detailMap = detail.get(0);

        if ((boolean) detailMap.get("result")) {

            String invokeResult = (String) detailMap.get("response");
            Map<String, Object> resultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(invokeResult, Map.class);

            if ((Double) resultMap.get("status") == 200.0) {

                Map<String, Object> response = (Map<String, Object>) resultMap.get("response");

                result = (Map<String, Object>) DWGsonProvider.getGson().fromJson(response.get("data").toString(), Map.class);
                installHistory = (List<Map<String, Object>>) result.get("installHistory");

                for (Map<String, Object> history : installHistory) {
                    String log = history.toString();
                    history.put("log", log);
                }

                DWDataSetOperationOption option = new DWDataSetOperationOption();
                option.setManagementFieldEnabled(false);

                List<Map<String, Object>> gIdResult = dao.select(option, SELECT_SQL, (String) param.get(_gatewayIdParam));
                if (gIdResult.size() > 0) {
                    if (this.getConnectStatus(param, param.get(_gatewayIdParam).toString(), apName).equals("updating")) {
                        DWServiceResult appUpdateLog = (DWServiceResult) this.getAppUpdateLog(param);
                        String log = (String) appUpdateLog.getData();
                        Map<String, Object> updateMap = new HashMap<>();
                        Timestamp updateDate = (Timestamp) gIdResult.get(0).get("updateDate");
                        String version = (String) gIdResult.get(0).get("version");
                        String userId = (String) gIdResult.get(0).get("userId");
                        String userName = (String) gIdResult.get(0).get("userName");
                        updateMap.put("deploy_version", version);
                        updateMap.put("install_date", updateDate);
                        updateMap.put("userId", userId);
                        updateMap.put("userName", userName);
                        updateMap.put("status", "updating");
                        updateMap.put("log", log);
                        installHistory.add(updateMap);
                    }
                }
            } else {
                _log.debug("[getLocalSiteInstallLog] ServiceClient response status !=200");
            }
        } else {
            _log.debug("[getLocalSiteInstallLog] ServiceClient response result=false");
        }
        Collections.reverse(installHistory);
        return DWServiceResultBuilder.build(true, installHistory);
    }

    /**
     * 比較版本
     *
     * @return
     * @throws Exception
     */
    public boolean getCompareVersion(Map<String, Object> param) throws Exception {

        String appId = (String) param.get(_appIdParam);
        String deploy_version = this.getAvailableUpdate(appId);

        boolean compareVersion = false;

        DWServiceResult result = (DWServiceResult) this.getAppVersion(param);
        if (result.isSuccess()) {
            Map<String, Object> versionMap = (Map<String, Object>) result.getData();

            String version = new String();
            if ((boolean) versionMap.get("result")) {
                version = (String) versionMap.get("version");
                if (!deploy_version.equals(version)) {
                    compareVersion = true;

                    DWDataSetOperationOption option = new DWDataSetOperationOption();
                    option.setManagementFieldEnabled(false);

                    List<Map<String, Object>> gIdResult = dao.select(option, SELECT_SQL, param.get("gId").toString());

                    if (gIdResult.size() > 0) {

                        Date date = new Date();
                        Date updateDate = (Date) gIdResult.get(0).get("updateDate");
                        String dbVersion = (String) gIdResult.get(0).get("version");

                        long between = (date.getTime() - updateDate.getTime()) / (1000);

                        if (between <= 600) {
                            compareVersion = false;
                        } else if (between > 600) {
                            compareVersion = true;
                        }
                    }
                }
            }
        }
        return compareVersion;
    }

    private String getConnectStatus(Map<String, Object> param, String lsGatewayId, String lsAppId) throws Exception {

        String lsConnectStatus = "true";
        String version = new String();

        param.put("gId", lsGatewayId);
        param.put("appId", lsAppId);

        DWServiceResult result = (DWServiceResult) this.getAppVersion(param);
        if (result.isSuccess()) {
            Map<String, Object> versionMap = (Map<String, Object>) result.getData();

            if ((boolean) versionMap.get("result")) {
                version = (String) versionMap.get("version");
            }
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> gIdResult = dao.select(option, SELECT_SQL, lsGatewayId);
        if (gIdResult.size() > 0) {

            Date date = new Date();
            Date updateDate = (Date) gIdResult.get(0).get("updateDate");
            String updateversion = (String) gIdResult.get(0).get("version");

            long between = (date.getTime() - updateDate.getTime()) / (1000);

            if (!updateversion.equals(version) && between <= 600 && !this.getCompareVersion(param)) {
                lsConnectStatus = "updating";
            } else {
                lsConnectStatus = "true";
            }
//            if (this.getCompareVersion(param)) {
//                lsConnectStatus = "true";
//            }
        }
        return lsConnectStatus;
    }
}
