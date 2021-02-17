package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.marketmanagement.dwmarketmanagement.service.IGoodsTypeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Miko
 */
public class GoodsTypeService implements IGoodsTypeService {

    private static final Log log = LogFactory.getLog(GoodsTypeService.class);

    @Override
    public List<Map<String, Object>> get(List<String> oids) throws Exception {
        if (oids == null || oids.isEmpty())
            throw new DWArgumentException("id", "id is null or empty!");

        String gmcUrl = DWModuleConfigUtils.getCurrentModuleProperty("gmcUrl");
        String goodsListApi = DWModuleConfigUtils.getCurrentModuleProperty("goodsListApi");

        // header
        String userToken = DWServiceContext.getContext().getToken();

        /**
         * 調用GMC api http://192.168.9.27:22615/api/gmc/v2/goods
         */
        String goodsListUrl = String.format("%s%s", gmcUrl, goodsListApi);

        log.info(">>>get goodsList url = " + goodsListUrl);

        HttpClient goodsListClient = HttpClientBuilder.create().build();
        HttpGet goodsListRequest = new HttpGet(goodsListUrl);
        goodsListRequest.setHeader("Content-Type", "application/json");
        goodsListRequest.setHeader("digi-middleware-auth-user", userToken);
        HttpResponse goodsListResponse = goodsListClient.execute(goodsListRequest);

        String goodsListContent = EntityUtils.toString(goodsListResponse.getEntity());
        int goodsListStatusCode = goodsListResponse.getStatusLine().getStatusCode();
        if (goodsListStatusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get GoodsTypeService List failed, status code = ").append(goodsListStatusCode)
                    .append(", please check the log for more information.");

            log.error("IGoodsTypeService.get failed! -> " + goodsListContent);

            throw new DWException(errorMessage.toString());
        }

        List<Map<String, Object>> goodsList = DWGsonProvider.getGson().fromJson(goodsListContent, ArrayList.class);
        List<Map<String, Object>> list = new ArrayList<>();

        for (String code : oids) {
            for (Map<String, Object> map : goodsList) {
                if (code.equals(map.get("code"))) {
                    list.add(map);
                    break;
                }
            }
        }

        List<Map<String, Object>> resultList = list.stream().distinct().collect(Collectors.toList());
        return resultList;
    }
}
