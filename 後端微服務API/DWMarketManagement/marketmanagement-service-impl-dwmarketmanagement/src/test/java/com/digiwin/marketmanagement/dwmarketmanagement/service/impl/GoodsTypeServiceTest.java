package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;


import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.app.service.DWServiceContext;
import com.google.gson.Gson;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Miko
 */

public class GoodsTypeServiceTest {
    @Tested
    GoodsTypeService goodsTypeService;

    @Mocked
    DWServiceContext dwServiceContext;

    @Mocked
    HttpResponse httpResponse;

    @Mocked
    StatusLine statusLine;

    @Mocked
    DefaultHttpClient httpClient;

    @Mocked
    HttpEntity httpEntity;

    @Mocked
    CloseableHttpResponse mockCloseableHttpResponse;

    @Mocked
    HttpGet httpGet;

    @Mocked
    HttpUriRequest httpUriRequest;

    @Mocked
    LogFactory logFactory;


    @Mocked
    DWModuleConfigUtils dwModuleConfigUtils;


    @Mocked
    HttpClientBuilder httpClientBuilder;

    @Mocked
    DWModuleMessageResourceBundleUtils dWModuleMessageResourceBundleUtils;

    @Test
    public void get() throws Exception {
        String mockToken = "9a80b5ab-5e09-4a34-b1c9-1bfc4f590622";

        List<String> list1 = new ArrayList<>();
        list1.add("B2BK");
        list1.add("135003");

        Gson gson = DWGsonProvider.getGson();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-goodsListContent.json")));
        List<Map<String, Object>> list = gson.fromJson(bufferedReader, List.class);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getRequestHeader() {
                        Map<String, Object> result = new HashMap<>();
                        result.put("token", mockToken);
                        return result;
                    }
                };

                new MockUp<EntityUtils>() {
                    public String toString(final HttpEntity entity) throws IOException, ParseException {
                        return null;
                    }
                };

                httpClient.execute(withAny(httpUriRequest));
                result = mockCloseableHttpResponse;

                HttpEntity entity = new StringEntity(list.toString(), "UTF-8");
                httpResponse.getEntity();
                result = entity;

                httpResponse.getStatusLine();
                result = statusLine;

                statusLine.getStatusCode();
                result = 200;
            }
        };

        //場景1：正常調用，入參有給、status 200

        goodsTypeService.get(list1);


        //場景2：入參沒給
        try {
            goodsTypeService.get(null);
        } catch (Exception e) {
            assertEquals("id is null or empty!", e.getMessage());
        }

        int mockStatusCode = 500;
        //場景3：status 500
        new Expectations() {
            {

                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getRequestHeader() {
                        Map<String, Object> result = new HashMap<>();
                        result.put("token", mockToken);
                        return result;
                    }
                };

                new MockUp<EntityUtils>() {
                    public String toString(final HttpEntity entity) throws IOException, ParseException {
                        return null;
                    }
                };

                httpClient.execute(withAny(httpUriRequest));
                result = mockCloseableHttpResponse;

                HttpEntity entity = new StringEntity(list.toString(), "UTF-8");
                httpResponse.getEntity();
                result = entity;

                httpResponse.getStatusLine();
                result = statusLine;

                statusLine.getStatusCode();
                result = mockStatusCode;
            }
        };

        try {
            goodsTypeService.get(list1);
        } catch (Exception e) {
            assertEquals("Get GoodsTypeService List failed, status code = " + mockStatusCode + ", please check the log for more information.", e.getMessage());
        }


    }
}