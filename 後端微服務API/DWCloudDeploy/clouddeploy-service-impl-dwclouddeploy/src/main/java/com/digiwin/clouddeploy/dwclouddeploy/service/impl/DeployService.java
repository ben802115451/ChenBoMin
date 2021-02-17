package com.digiwin.clouddeploy.dwclouddeploy.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.clouddeploy.dwclouddeploy.service.IDeployService;
import com.digiwin.app.container.exceptions.DWException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.*;

public class DeployService implements IDeployService {

    private static final Log log = LogFactory.getLog(DeployService.class);

    // *********************************************取得全部區域版本******************************************************

    public Object getProductVersion(String appId) throws Exception {

        String module = DWServiceContext.getContext().getModuleName();
        String cloudServiceUrl = DWModuleConfigUtils.getProperty(module, "cloudServiceUrl");
        String productVersionApi = DWModuleConfigUtils.getProperty(module, "productVersionApi");
        String url = String.format("%s%s?appId=%s", cloudServiceUrl, productVersionApi, appId);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get Product Version failed, status code = ").append(statusCode)
                    .append(", please check the log for more information.");

            log.error("IDeployService.getProductVersion failed! -> " + content);
            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> invocationResultMap = DWGsonProvider.getGson().fromJson(content, Map.class);
        Map<String, Object> dapResultMap = (Map<String, Object>) invocationResultMap.get("response");

        return DWServiceResultBuilder.build(dapResultMap.get("data")); // 單純調用回傳data資料

    }

    // *********************************************取得特定區間版本******************************************************

    public Object getSpecificVersion(String imageName, String maxfullImage, String minfullImage, String orderBy)
            throws Exception {

        String module = DWServiceContext.getContext().getModuleName();

        String cloudServiceUrl = DWModuleConfigUtils.getProperty(module, "cloudServiceUrl");
        String specificVersionApi = DWModuleConfigUtils.getProperty(module, "specificVersionApi");
        String url = String.format("%s%s?imageName=%s&maxImageName=%s&minImageName=%s&orderBy=%s", cloudServiceUrl,
                specificVersionApi, imageName, maxfullImage, minfullImage, orderBy);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get Specific Version failed, status code = ").append(statusCode)
                    .append(", please check the log for more information.");

            log.error("IDeployService.getSpecificVersion failed! -> " + content);
            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> invocationResultMap = DWGsonProvider.getGson().fromJson(content, Map.class);
        Map<String, Object> dapResultMap = (Map<String, Object>) invocationResultMap.get("response");

        dapResultMap.remove("pullUrl"); // Remove原先用不到的Array -> pullUrl[]

        return DWServiceResultBuilder.build(dapResultMap);
    }

    // *********************************************取得單一部屬歷程******************************************************

    public Object getLog(String imageName, String cloud, String area, String appId, String action, String id)
            throws Exception {

        String module = DWServiceContext.getContext().getModuleName();

        String cloudServiceUrl = DWModuleConfigUtils.getProperty(module, "cloudServiceUrl");
        String logApi = DWModuleConfigUtils.getProperty(module, "logApi");
        String url = String.format("%s%s?imageName=%s&cloud=%s&area=%s&appId=%s&action=%s&id=%s", cloudServiceUrl,
                logApi, imageName, cloud, area, appId, action, id);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get Log failed, status code = ").append(statusCode)
                    .append(", please check the log for more information.");

            log.error("IDeployService.getLog failed! -> " + content);
            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> invocationResultMap = DWGsonProvider.getGson().fromJson(content, Map.class);
        Map<String, Object> dapResultMap = (Map<String, Object>) invocationResultMap.get("response");

        return DWServiceResultBuilder.build(dapResultMap); // 單純調用回傳response資料
    }

    // *********************************************取得當前部屬狀態******************************************************

    public Object getLogView(String imageName, String cloud, String area, String appId, String action)
            throws Exception {

        String module = DWServiceContext.getContext().getModuleName();

        String cloudServiceUrl = DWModuleConfigUtils.getProperty(module, "cloudServiceUrl");
        String logViewApi = DWModuleConfigUtils.getProperty(module, "logViewApi");
        String url = String.format("%s%s?imageName=%s&cloud=%s&area=%s&appId=%s&action=%s", cloudServiceUrl, logViewApi,
                imageName, cloud, area, appId, action);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get Log failed, status code = ").append(statusCode)
                    .append(", please check the log for more information. -> " + content);

            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> invocationResultMap = DWGsonProvider.getGson().fromJson(content, Map.class);

        return DWServiceResultBuilder.build(invocationResultMap.get("response")); // 單純調用回傳 jenkins url
    }

    // **********************************************部屬新版*******************************************************

    public Object postDeployment(String cloud, String area, String appId, String fullImage) throws Exception {

        if (fullImage == null) { // 判斷 fullImage 是否為空指針
            throw new DWArgumentException("fullImage", "fullImage is null or empty");
        }
        String module = DWServiceContext.getContext().getModuleName();

        Map<String, Object> profile = DWServiceContext.getContext().getProfile(); // 取得調用的"userId"以及"userName"

        String userId = (String) profile.get("userId");
        String userName = (String) profile.get("userName");

        String cloudServiceUrl = DWModuleConfigUtils.getProperty(module, "cloudServiceUrl");
        String deploymentApi = DWModuleConfigUtils.getProperty(module, "deploymentApi");
        String url = String.format("%s%s", cloudServiceUrl, deploymentApi);

        JSONObject postData = new JSONObject();
        postData.put("cloud", cloud);
        postData.put("area", area);
        postData.put("appId", appId);
        postData.put("images", fullImage);
        postData.put("userId", userId);
        postData.put("userName", userName);
        HttpEntity entity = new StringEntity(postData.toString());

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);

        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Post Deployment failed, status code = ").append(statusCode)
                    .append(", please check the log for more information.");

            log.error("IDeployService.postDeployment failed! -> " + content);
            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> invocationResultMap = DWGsonProvider.getGson().fromJson(content, Map.class);

        return DWServiceResultBuilder.build(invocationResultMap.get("response")); // 回傳部屬response結果
    }

    // **********************************************重新部屬*******************************************************

    public Object postRedployment(String cloud, String area, String appId, String imageName) throws Exception {

        String fullImage = null;

        DWServiceResult result = (DWServiceResult) this.getProductVersion(appId);
        List<Map<String, Object>> siteList = (List<Map<String, Object>>) result.getData();

        for (Map<String, Object> siteItem : siteList) { // ex. oeefrontend, oeebackend -> siteList
            if (siteItem.get("imageName") == null) { // 判斷 appId 是否有對應之 imageName
                throw new Exception("IDeployService.postRedployment failed! -> " + appId
                        + " can't found the corresponding " + imageName);
            }
            if (siteItem.get("imageName").equals(imageName)) { // get target site -> ex. imageName = oeefrontend
                Map<String, Object> cloudsMap = (Map<String, Object>) siteItem.get("imagedata");
                List<Map<String, Object>> areaList = (List<Map<String, Object>>) cloudsMap.get(cloud);
                if (areaList == null || areaList.isEmpty()) { // 判斷 appId 是否有對應之 cloud
                    throw new Exception("IDeployService.postRedployment failed! -> " + appId
                            + " can't found the corresponding " + cloud);
                }
                for (Map<String, Object> areaItem : areaList) { // ex. test, paas, pre, prod -> areaList
                    if (areaItem.get("area") == null) { // 判斷 appId 是否有對應之 area
                        throw new Exception("IDeployService.postRedployment failed! -> " + appId
                                + " can't found the corresponding " + area);
                    }
                    if (areaItem.get("area").equals(area)) { // get target area -> ex. area = paas
                        fullImage = (String) areaItem.get("fullImage");
                    }
                }
            }
        }
        DWServiceResult Logresult = (DWServiceResult) this.getLog(imageName, cloud, area, appId, "1", "");
        Map<String, Object> Logresult2 = (Map<String, Object>) Logresult.getData();

        if (Logresult2.get("status").equals("IN_PROGRESS")) { // 透過調用部屬歷程Api判斷當前狀態是否為部屬中
            throw new Exception("IDeployService.postDeployment failed! -> " + "目前為正在部屬中狀態，無法進行重新部屬");
        }
        return this.postDeployment(cloud, area, appId, fullImage);
    }
}