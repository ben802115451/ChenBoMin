package com.digiwin.developer.dwdeveloper.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.service.DWServiceResult;
import com.google.gson.Gson;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GoodsThemeServiceTest {

    @Tested
    GoodsThemeService target;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dao = new DWDaoImpl(queryRunner);

    DWDataSet dataset;

    @Mocked
    DWDataSetBuilder mockDWDataSetBuilder;

    @Mocked
    DWMetadataContainer mockDWMetadataContainer;

    @Mocked
    DWDataColumnCollection mockDWDataColumnCollection;


    static final String GOODS_THEME = "goods_theme";
    static final String GOODS_THEME_DETAIL = "goods_theme_detail";

    @Test
    void post() throws Exception {

        new Expectations() {
            {
                {
                    dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                    result = dataset;
                }
            }
        };
        DWServiceResult postResult = (DWServiceResult) target.post(dataset);

        assertTrue(postResult.getSuccess());
        assertEquals("post success", postResult.getMessage());
    }

    @Test
    void put() throws Exception {

        new Expectations() {
            {
                {
                    dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                    result = dataset;
                }
            }
        };
        DWServiceResult putResult = (DWServiceResult) target.put(dataset);

        assertTrue(putResult.getSuccess());
        assertEquals("put success", putResult.getMessage());
    }

    @Test
    void getList() throws Exception {

        DWPagableQueryInfo queryInfo = new DWPagableQueryInfo(GOODS_THEME);

        new Expectations() {
            {
                {
                    dao.selectWithPage(queryInfo, (DWDataSetOperationOption) any);
                    result = dataset;
                }
            }
        };

        DWServiceResult getListResult = (DWServiceResult) target.getList(queryInfo);

        assertTrue(getListResult.getSuccess());
    }

    @Test
    void delete() throws Exception {

        List<Object> oids = new ArrayList<>();
        oids.add(1.0);

        String sampleOrgRowsAJsonString = "[{themeid=1, status=1}]";

        Gson gson = new Gson();
        List<Map<String, Object>> orgRowsA = gson.fromJson(sampleOrgRowsAJsonString, List.class);

        DWDataTableBuilder builder = new DWDataTableBuilder().setName(GOODS_THEME).addRowOrgDatas(orgRowsA);

        dataset = builder.create().getDataSet();

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        assertThrows(DWBusinessException.class, () -> target.delete(oids));

        dataset = new DWDataSet();
        dataset.newTable(GOODS_THEME);

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;

                dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                result = any;
            }
        };

        DWServiceResult deleteResult = (DWServiceResult) target.delete(oids);
        assertTrue(deleteResult.getSuccess());
    }

    @Test
    void getDisplay() throws Exception {

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $init() {
                    }

                    @Mock
                    void $clinit() {
                    }
                };

                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };

        DWServiceResult getDisplayResult = (DWServiceResult) target.getDisplay();
        assertTrue(getDisplayResult.getSuccess());
    }

    @Test
    void putDisplay() throws Exception {

        Integer oid = 1;

        new Expectations() {
            {
                dao.update((DWDataSetOperationOption) any, anyString, oid);
                result = 1;
            }
        };
        DWServiceResult putDisplayResult = (DWServiceResult) target.putDisplay(oid);
        assertTrue(putDisplayResult.getSuccess());

        assertThrows(DWArgumentException.class, () -> target.putDisplay(null));
    }

    @Test
    void putClose() throws Exception {

        Integer oid = 1;

        new Expectations() {
            {
                dao.update((DWDataSetOperationOption) any, anyString, oid);
                result = 1;
            }
        };
        DWServiceResult putCloseResult = (DWServiceResult) target.putClose(oid);
        assertTrue(putCloseResult.getSuccess());

        assertThrows(DWArgumentException.class, () -> target.putClose(null));
    }

    @Test
    void postGoods() throws Exception {
        assertThrows(DWArgumentException.class, () -> target.postGoods(null, dataset));

        String sampleOrgRowsAJsonString = "[{goodsCode=testGoods}]";

        Gson gson = new Gson();
        List<Map<String, Object>> orgRowsA = gson.fromJson(sampleOrgRowsAJsonString, List.class);

        DWDataTableBuilder builder = new DWDataTableBuilder().setName(GOODS_THEME_DETAIL).addRowOrgDatas(orgRowsA);

        dataset = builder.create().getDataSet();

        DWServiceResult putCloseResult = (DWServiceResult) target.postGoods(1, dataset);
        assertTrue(putCloseResult.getSuccess());
        assertEquals((Integer)1, dataset.getTable(GOODS_THEME_DETAIL).getRow(0).get("themeid"));
    }

    @Test
    void get() throws Exception {

        List<Integer> oids = new ArrayList<>();
        oids.add(1);

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = any;
            }
        };
        target.get(oids);
    }

    @Test
    void getUsedGoodsList() throws Exception {

        List<Map<String, Object>> daoResult = new ArrayList<>();
        Map<String, Object> goods = new HashMap<>();
        goods.put("themeName", "testThemeName");
        goods.put("goodsCode", "testGoods");
        daoResult.add(goods);

        new Expectations() {
            {
                dao.select((DWDataSetOperationOption) any, anyString);
                result = daoResult;
            }
        };
        List<Map<String, Object>> getUsedGoodsListResult = target.getUsedGoodsList();
        assertEquals(1, getUsedGoodsListResult.size());
        assertEquals("testThemeName", getUsedGoodsListResult.get(0).get("themeName"));
        assertEquals("testGoods", getUsedGoodsListResult.get(0).get("goodsCode"));
    }
}