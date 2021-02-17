package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.loader.DWMetadataLoaderManager;
import com.digiwin.app.module.spring.SpringContextUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MediaAreaServiceTest {

    @Tested
    MediaAreaService target;

    @Injectable
    DWDao dao;

    @Mocked
    SpringContextUtils mockSpringContextUtils;

    @Mocked
    DWMetadataLoaderManager mockDWMetadataLoaderManager;

    @Mocked
    DWDataRowCollection mockDWDataRowCollection;

    static String TABLE = "layout_arrangement";

    @Test
    @DisplayName("post")
    void post() throws Exception {

        //場景1 : 成功更新

        String userId = "ben802115451";
        String userName = "Bomin";

        DWDataSet dataset = new DWDataSet();
        dataset.newTable(TABLE).newRow().set("areaType", "V");

        Map<String, Object> data = new HashMap<>();
        data.put("areaType", "V");
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(0, data);

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    public Map<String, Object> getSourceMap() {
                        Map<String, Object> datasetTableMap = new HashMap<String, Object>();
                        datasetTableMap.put(TABLE, dataList);
                        return datasetTableMap;
                    }
                };

                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put("userId", userId);
                        result.put("userName", userName);
                        return result;
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
        assertEquals("視頻或活動新增成功", postResult.geMessage());

        //場景2 : 輸入其他"areaType"

        data.put("areaType", "C");

//        dataList = (List<Map<String, Object>>) dataset.getSourceMap().get(TABLE);
//        dataList.add(0, data);

        try {
            target.post(dataset);
        } catch (DWArgumentException e) {
            assertTrue(e.getMessage().equals("areaType請輸入V or A"));
        }

        //場景2 : 沒有輸入"areaType"

        data.remove("areaType");

//        dataList = (List<Map<String, Object>>) dataset.getSourceMap().get(TABLE);
//        dataList.add(0, data);

        try {
            target.post(dataset);
        } catch (DWArgumentException e) {
            assertTrue(e.getMessage().equals("請輸入areaType"));
        }
    }

    @Test
    @DisplayName("get")
    void get() throws Exception {

        DWDataSet dataset = new DWDataSet();
        DWDataRow row = dataset.newTable(TABLE).newRow();

        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("lastModifyUserId", "ben802115451");
        data.put("lastModifyUserName", "Bomin");
        data.put("areaType", "M-V");

        new Expectations() {
            {
                {
                    dao.selectOne((DWQueryInfo) any, (DWDataSetOperationOption) any);
                    result = row;
                }
                {
                    row.getData();
                    result = data;
                }
            }
        };

        DWServiceResult getResult = (DWServiceResult) target.get();
        Map<String, Object> mapResult = (Map<String, Object>) getResult.getData();

        assertTrue(getResult.getSuccess());
        assertEquals(true, getResult.getSuccess());
        assertEquals(1, mapResult.get("id"));
        assertEquals("ben802115451", mapResult.get("lastModifyUserId"));
        assertEquals("Bomin", mapResult.get("lastModifyUserName"));
        assertEquals("V", mapResult.get("areaType"));

    }
}