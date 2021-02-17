package com.digiwin.deploy.service.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.deploy.service.IInstallService;

public class InstallService implements IInstallService {

    private static final Log log = LogFactory.getLog(InstallService.class);

//	static String GET_URL = "http://registry-test.10.40.46.119.nip.io/restful/service/Registry/Project/AllImage?imageName=";

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    // 取得正式發佈的最新版和相對應檔案ID
    @Override
    public Object getPublishReleased(String appId, String deployArea) throws Exception {

        if (appId == null || appId.isEmpty())
            throw new DWArgumentException("appId", "appId is null or empty!");
        if (deployArea == null || deployArea.isEmpty())
            throw new DWArgumentException("deployArea", "deployArea is null or empty!");

        DWQueryInfo queryInfo = new DWQueryInfo(DBConstants.DEPLOY_INFO);
        queryInfo.addEqualInfo(DBConstants.APP_ID, appId);
        queryInfo.addEqualInfo(DBConstants.DEPLOY_AREA, deployArea);
        queryInfo.addEqualInfo(DBConstants.PUBLISH_STATUS, 1);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.addCascadeQuery(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL);
        option.addCascadeQuery(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_PARAMETERS);
        option.setManagementFieldEnabled(false);

        Object result = this.dao.select(queryInfo, option);

        return DWServiceResultBuilder.build(result);
    }

    // 取得指定發佈的版本和相對應檔案ID
    @Override
    public Object getSpecificReleased(String appId, String deployArea, String deploySite, String version)
            throws Exception {

        if (appId == null || appId.isEmpty()) {
            throw new DWArgumentException("appId", "appId is null or empty!");
        }

        if (deployArea == null || deployArea.isEmpty()) {
            throw new DWArgumentException("deployArea", "deployArea is null or empty!");
        }

        if (deploySite == null || deploySite.isEmpty()) {
            throw new DWArgumentException("deploySite", "deploySite is null or empty!");
        }

        if (version == null || version.isEmpty()) {
            throw new DWArgumentException("version", "version is null or empty!");
        }

        DWQueryInfo queryInfo = new DWQueryInfo(DBConstants.DEPLOY_INFO);
        queryInfo.addEqualInfo(DBConstants.APP_ID, appId);
        queryInfo.addEqualInfo(DBConstants.DEPLOY_AREA, deployArea);
        queryInfo.addEqualInfo(DBConstants.DEPLOY_SITE, deploySite);
        queryInfo.addEqualInfo(DBConstants.DEPLOY_VERSION, version);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.addCascadeQuery(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL);
        option.addCascadeQuery(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_PARAMETERS);
        option.setManagementFieldEnabled(false);

        Object result = this.dao.select(queryInfo, option);
        return DWServiceResultBuilder.build(result);
    }

    // 地端取得[最新]的可更新的版本
    // 即取得地端publish_status為1的版號
    // 區綁端只能有一筆為1
    @Override
    public Object getAvailableUpdate(String appId, String deployArea) throws Exception {

        DWQueryInfo queryInfo = new DWQueryInfo(DBConstants.DEPLOY_INFO);
        queryInfo.setSelectFields(Arrays.asList(DBConstants.APP_ID, DBConstants.DEPLOY_AREA, DBConstants.DEPLOY_SITE,
                DBConstants.DEPLOY_VERSION));

        queryInfo.addEqualInfo(DBConstants.APP_ID, appId);
        queryInfo.addEqualInfo(DBConstants.DEPLOY_AREA, deployArea);
        queryInfo.addEqualInfo(DBConstants.PUBLISH_STATUS, "1");

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.select(queryInfo, option);

        return DWServiceResultBuilder.build(result);
    }

    // 向Repository取得image檔案列表
    @SuppressWarnings("unchecked")
    @Override
    public Object getImageNameList(String appId, String deploySite) throws Exception {

        if (appId == null || appId.isEmpty())
            throw new DWArgumentException("appId", "appId is null or empty!");
        if (deploySite == null || deploySite.isEmpty())
            throw new DWArgumentException("deploySite", "deploySite is null or empty!");

        String module = DWServiceContext.getContext().getModuleName();

        String dockerRegistryUrl = DWModuleConfigUtils.getProperty(module, "dockerRegistryUrl");
        String repositoryListApi = DWModuleConfigUtils.getProperty(module, "repositoryListApi");
        String imageListApi = DWModuleConfigUtils.getProperty(module, "imageListApi");

        String imageNameType = this.getImageNameType(deploySite);

        String url = String.format("%s%s?projectName=%s&deploymentName=%s&type=%s", dockerRegistryUrl, repositoryListApi, appId, appId, imageNameType);

        log.info(">>>get repositoryList url = " + url);
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("status code = ").append(statusCode)
                    .append(", ").append(content);

            log.error("IInstallService.getImageNameList failed! -> " + content);

            throw new DWException(errorMessage.toString());
        }

        List<String> invocationResultList = DWGsonProvider.getGson().fromJson(content, List.class);

        List<String> imageNameList = new ArrayList<>();
        boolean success = true;
        String message = "";
        if (invocationResultList.size() > 0) {
            for (String imageName : invocationResultList) {
                Map<String, Object> tagResult = this.getTagList(imageName);
                List<String> tagList = (List<String>) tagResult.get("imageNameList");
                success = (boolean) tagResult.get("success");
                message = (String) tagResult.get("message");

                imageNameList.addAll(tagList);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sourceApi", imageListApi);
//         因為回到文檔中心取file_id會出現500錯誤，所以把pullUrl改成imageName+tag
        result.put("imageList", imageNameList);
        return DWServiceResultBuilder.build(success, message, result);
    }

    /**
     * 取得Tag List
     *
     * @param imageName
     * @return
     */
    private Map<String, Object> getTagList(String imageName) throws Exception {

        String module = DWServiceContext.getContext().getModuleName();
        String dockerRegistryUrl = DWModuleConfigUtils.getProperty(module, "dockerRegistryUrl");
        String imageListApi = DWModuleConfigUtils.getProperty(module, "imageListApi");

        String url = String.format("%s%s?imageName=%s&orderBy=DESC", dockerRegistryUrl, imageListApi, imageName);

        log.info(">>>get imageList url = " + url);
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        String content = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get Image List failed, status code = ").append(statusCode)
                    .append(", please check the log for more information.");

            log.error("IInstallService.getImageNameList failed! -> " + content);

            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> invocationResultMap = DWGsonProvider.getGson().fromJson(content, Map.class);
        Map<String, Object> dapResultMap = (Map<String, Object>) invocationResultMap.get("response");

        List<Object> pullUrlList = null;
        if (dapResultMap.containsKey("pullUrl")) {

            pullUrlList = (List<Object>) dapResultMap.get("pullUrl");
        }
        List<String> imageNameList = new ArrayList<>();
        if (dapResultMap.containsKey("tag")) {
            List<String> tagList = (List<String>) dapResultMap.get("tag");
            if (tagList != null) {
                for (int i = 0; i < tagList.size(); i++) {
                    imageNameList.add(imageName + ":" + tagList.get(i));
                }
            }
        }
        // 2019-6-6 falcon 如果 repository 找不到 那這個 api 回傳的 pullUrl 會是 null
        boolean success = pullUrlList != null;
        String message;
        if (success) {
            message = String.format("獲取儲存區  %s 中的映像檔清單成功!", imageName.toLowerCase());
        } else {
            message = String.format("無法找指定的儲存區  %s !", imageName.toLowerCase());
            pullUrlList = Collections.emptyList();
        }

        Map<String, Object> tagResult = new HashMap<>();
        tagResult.put("imageNameList", imageNameList);
        tagResult.put("message", message);
        tagResult.put("success", success);

        return tagResult;
    }

    /**
     * 取得 image 名稱的類型
     * <p>
     *
     * @param deploySite 佈署端
     * @return image name 的類型
     */
    private String getImageNameType(String deploySite) {

        String imageType = "unknown";

        if (DBConstants.DEPLOY_AREA_FRONTEND.equalsIgnoreCase(deploySite)) {

            imageType = "frontend";
        } else if (DBConstants.DEPLOY_AREA_BACKEND.equalsIgnoreCase(deploySite)) {

            imageType = "backend";
        }

        return imageType;
    }
}
