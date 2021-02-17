package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.loader.DWMetadataLoaderManager;
import com.digiwin.app.module.spring.SpringContextUtils;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecommendedProductsServiceTest {

    @Tested
    RecommendedProductsService target;

    @Injectable
    DWDao dao;

    @Injectable
    GoodsTypeService goodsTypeService;

    @Mocked
    SpringContextUtils mockSpringContextUtils;

    @Mocked
    DWMetadataLoaderManager mockDWMetadataLoaderManager;

    @Mocked
    DWDataRowCollection mockDWDataRowCollection;

    static String TABLE = "recommended_products";

    @Test
    @DisplayName("post")
    void post() throws Exception {

        //場景1 : 成功更新

        DWDataSet dataset = new DWDataSet();
        dataset.newTable(TABLE);

        Map<String, Object> data = new HashMap<>();
        data.put("goodsCode", "test");


        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    public Map<String, Object> getSourceMap() {
                        List<Map<String, Object>> dataList = new ArrayList<>();
                        dataList.add(0, data);

                        Map<String, Object> datasetTableMap = new HashMap<String, Object>();
                        datasetTableMap.put(TABLE, dataList);
                        return datasetTableMap;
                    }
                };

                {
                    dao.execute(dataset, (DWDataSetOperationOption) any);
                    result = new DWSQLExecutionResult();
                }
            }
        };

        DWServiceResult postResult = (DWServiceResult) target.post(dataset);

        assertTrue(postResult.getSuccess());
        assertEquals("推薦商品更新成功", postResult.geMessage());

        //場景2 : 商品超過10項

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    public Map<String, Object> getSourceMap() {
                        List<Map<String, Object>> dataList = new ArrayList<>();
                        dataList.add(0, new HashMap<>());
                        dataList.add(1, new HashMap<>());
                        dataList.add(2, new HashMap<>());
                        dataList.add(3, new HashMap<>());
                        dataList.add(4, new HashMap<>());
                        dataList.add(5, new HashMap<>());
                        dataList.add(6, new HashMap<>());
                        dataList.add(7, new HashMap<>());
                        dataList.add(8, new HashMap<>());
                        dataList.add(9, new HashMap<>());
                        dataList.add(10, new HashMap<>());
                        Map<String, Object> datasetTableMap = new HashMap<String, Object>();
                        datasetTableMap.put(TABLE, dataList);
                        return datasetTableMap;
                    }
                };
            }
        };

        try {
            target.post(dataset);
        } catch (DWArgumentException e) {
            assertTrue(e.getMessage().equals("商品最多不超過10項!"));
        }
    }

    @Test
    @DisplayName("get")
    void get() throws Exception {

        List<Map<String, Object>> resultData = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();

        data1.put("seq", 1);
        data1.put("goodsCode", "DOP");
        resultData.add(data1);

        data2.put("seq", 2);
        data2.put("goodsCode", "sampleApp1Service");
        resultData.add(data2);

        List<String> goodsCode = new ArrayList<>();
        goodsCode.add("DOP");
        goodsCode.add("sampleApp1Service");

        new Expectations() {
            {
                {
                    dao.select((DWDataSetOperationOption) any, anyString);
                    result = resultData;
                }
                {
                    List<Map<String, Object>> dataResult = new ArrayList<>();
                    data1.put("categoryId", "app");
                    data1.put("code", "DOP");

                    data2.put("categoryId", "service");
                    data2.put("code", "sampleApp1Service");

                    dataResult.add(data1);
                    dataResult.add(data2);

                    goodsTypeService.get(goodsCode);
                    result = dataResult;
                }
            }
        };

        DWServiceResult getResult = (DWServiceResult) target.get();
        List<Map<String, Object>> mapResult = (List<Map<String, Object>>) getResult.getData();

        assertTrue(getResult.getSuccess());
        assertEquals(true, getResult.getSuccess());
        assertEquals(1, mapResult.get(0).get("seq"));
        assertEquals("DOP", mapResult.get(0).get("goodsCode"));
        assertEquals("app", mapResult.get(0).get("categoryId"));

        new Expectations() {
            {
                {
                    dao.select((DWDataSetOperationOption) any, anyString);
                    result = new ArrayList<>();
                }
            }
        };

        getResult = (DWServiceResult) target.get();

        assertTrue(getResult.getSuccess());
        assertEquals(true, getResult.getSuccess());
    }
}