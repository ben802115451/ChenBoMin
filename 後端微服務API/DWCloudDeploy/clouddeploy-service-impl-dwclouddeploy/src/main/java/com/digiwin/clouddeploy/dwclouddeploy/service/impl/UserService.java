package com.digiwin.clouddeploy.dwclouddeploy.service.impl;

import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.clouddeploy.dwclouddeploy.service.IUserService;
import com.digiwin.data.permission.*;
import com.digiwin.data.service.DWDataPermission;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {
    private static final Log log = LogFactory.getLog(UserService.class);
    static final String MODULE_ID = "boss-operations";
    static final String ACTION_ID = "boss-flow-deploy";

    static class InvocationItem {
        public String name;
        @SerializedName("id")
        public String goodsCode;
        public String categoryId = "app";
    }

    static class Invocation{
        public ArrayList<InvocationItem> list;
    }

    static class FilterValue {
        public Long sid;
        public String filterField;
        public List<String> filterValue = new ArrayList<>();
    }

    static class RowPermission {
        public String filterType;
        public List<FilterValue> filterValue = new ArrayList<>();
        public Long sid;
    }

    static class UserPermission {
        public Boolean superadmin;
        public RowPermission rowPermission;
    }

    private static Gson customGson = null;

    private static synchronized Gson getCustomGson() {

        if (UserService.customGson == null) {

            GsonBuilder builder = new GsonBuilder().serializeNulls();
            builder.registerTypeAdapter(DWRowPermissionElement.class, new DWRowPermissionElementDeserializer());

            UserService.customGson = builder.create();
        }

        return UserService.customGson;
    }

    //取得所有應用
    @Override
    public Object getAppList() throws Exception {

        String module = DWServiceContext.getContext().getModuleName();
        String gmcUrl = DWModuleConfigUtils.getProperty(module, "gmcUrl");
        String appListApi = DWModuleConfigUtils.getProperty(module, "appListApi");

        /**
         *調用GMC api http://gmc-paas.digiwincloud.com.cn/api/gmc/v2/goods/code?pageSize=99999&categoryId=app
         * pageSize：設定顯示筆數
         * categoryId=app 取得categoryId為app的應用
         */
        String appListurl = String.format("%s%s?pageSize=99999&categoryId=app",gmcUrl , appListApi);

        log.info(">>>get appList url = " + appListurl);
        HttpClient appClient = new DefaultHttpClient();
        HttpGet appRequest = new HttpGet(appListurl);
        HttpResponse appResponse = appClient.execute(appRequest);

        String appContent = EntityUtils.toString(appResponse.getEntity());
        int appStatusCode = appResponse.getStatusLine().getStatusCode();
        if (appStatusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get App List failed, status code = ").append(appStatusCode)
                    .append(", please check the log for more information.");

            log.error("IUserService.getAppList failed! -> " + appContent);

            throw new DWException(errorMessage.toString());
        }

        Invocation invocationRes = UserService.getCustomGson().fromJson(appContent,Invocation.class);

        // new code
        DWUserPermission userPermission = (DWUserPermission) new DWDataPermission().getDataPermission(MODULE_ID,ACTION_ID);;
        if (userPermission.isSuperadmin()) {

            return DWServiceResultBuilder.build(invocationRes.list);
        }
        else {

            DWRowPermissionDefaultMatchOption option = new DWRowPermissionDefaultMatchOption();

            List<InvocationItem> userViews = userPermission.getRowPermission().filter(invocationRes.list, option);
            return DWServiceResultBuilder.build(userViews);
        }
    }
}
