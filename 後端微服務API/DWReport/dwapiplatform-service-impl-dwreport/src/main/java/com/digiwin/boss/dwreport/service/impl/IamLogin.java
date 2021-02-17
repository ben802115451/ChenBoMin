package com.digiwin.boss.dwreport.service.impl;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.Map;

import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class IamLogin {

    static String token = null;

    public static Map<String, Object> sendPost(String iamUserId, String iamPassword) {

        String iamUrl = DWApplicationConfigUtils.getProperty("iamUrl");
        String iamLoginApi = DWApplicationConfigUtils.getProperty("iamUrlLogin");
        String iamLoginUrl = String.format("%s%s", iamUrl, iamLoginApi);
        Map<String, Object> resultMap = new HashMap<>();
        HttpPost post = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();// 获取DefaultHttpClient请求
            post = new HttpPost(iamLoginUrl);
            // 构造消息头
            post.setHeader("Content-type", "application/json; charset=utf-8");

            JSONObject postData = new JSONObject();
            postData.put("userId", iamUserId);
            postData.put("password", iamPassword);
            postData.put("identityType", "query");

            StringEntity entity = new StringEntity(postData.toString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            // 发送Json格式的数据请求
            entity.setContentType("application/json");
            post.setEntity(entity);
            post.setHeader("digi-middleware-auth-app",
                    DWApplicationConfigUtils.getProperty("iamApToken"));
            HttpResponse response = httpClient.execute(post);
            String iamContent = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                // 请求和响应都成功了
                HttpEntity entityData = response.getEntity();
                String responseData = EntityUtils.toString(entityData, "utf-8");
                resultMap = DWGsonProvider.getGson().fromJson(iamContent, Map.class);
                System.out.println(resultMap);

            } else {
                resultMap = DWGsonProvider.getGson().fromJson(iamContent, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
