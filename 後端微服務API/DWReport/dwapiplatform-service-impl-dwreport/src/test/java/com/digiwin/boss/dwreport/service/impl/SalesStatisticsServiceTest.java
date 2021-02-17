package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.DWContainerContext;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.boss.dwreport.permission.DataPermissionService;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SalesStatisticsServiceTest {

    @Tested
    SalesStatisticsService target;

    @Mocked
    DWServiceContext mockDWServiceContext;

    @Mocked
    DataPermissionService mockDataPermissionService;

    @Injectable
    SalesStatisticsHistoryService salesStatisticsHistory;

    @Injectable
    ExcludeOrderService excludeOrderService;

    @Injectable
    DWContainerContext containerContext;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dao = new DWDaoImpl(queryRunner);

    @Test
    @DisplayName("putAssignedDate")
    void test_putAssignedDate_case() throws Exception {

        //場景1 : 成功更新

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> putAssignedDateResult = new HashMap<>();

        new Expectations() {
            {
                new MockUp<SalesStatisticsService>() {
                    @Mock
                    Object getAssignedDate(Map<String, Object> params) {
                        List<Map<String, Object>> resultList = new ArrayList<>();
                        resultMap.put("orderCode", "DGW2019102800001");
                        resultMap.put("orderSource", "DigiwinCloud");
                        resultMap.put("payDate", "2019-10-28");
                        resultMap.put("payCode", "2019102822001416351000310194");
                        resultMap.put("goodsCode", "E100001");
                        resultMap.put("goodsName", "E10维护合约");
                        resultMap.put("strategyCode", "E100000100001");
                        resultMap.put("strategyName", "E10wei");
                        resultMap.put("categoryId", "service");
                        resultMap.put("payPrice", 100.0);
                        resultMap.put("payMethod", "1");
                        resultMap.put("orderMode", 1.0);
                        resultMap.put("quantity", 1.0);
                        resultMap.put("orderCount", 1);
                        resultList.add(resultMap);

                        putAssignedDateResult.put("success", true);
                        putAssignedDateResult.put("message", "get success");
                        putAssignedDateResult.put("list", resultList);
                        return putAssignedDateResult;
                    }
                };
                dao.update((DWDataSetOperationOption) any, anyString, resultMap.get("orderSource"), resultMap.get("payDate"), resultMap.get("categoryId"), resultMap.get("goodsCode"), resultMap.get("goodsName"), resultMap.get("strategyCode"), resultMap.get("strategyName"), resultMap.get("orderMode"), resultMap.get("orderCount"), resultMap.get("quantity"), resultMap.get("payPrice"), resultMap.get("payMethod"));
                result = 1;
            }
        };
        String result = (String) target.putAssignedDate("2020-02-10");
        assertEquals("Success!  A total of 1 new data", result);

        //場景2 : updateStatus = 0

        new Expectations() {
            {
                new MockUp<SalesStatisticsService>() {
                    @Mock
                    Object getAssignedDate(Map<String, Object> params) {
                        List<Map<String, Object>> resultList = new ArrayList<>();
                        resultMap.put("orderMode", 3.0);
                        resultList.add(resultMap);

                        putAssignedDateResult.put("list", resultList);
                        return putAssignedDateResult;
                    }
                };
            }
        };
        try {
            target.putAssignedDate("2020-02-10");
        } catch (DWArgumentException e) {
            assertTrue(e.getMessage().equals("Update Failed!"));
        }

        //場景3 : Update Failed!

        putAssignedDateResult.remove("success");

        new Expectations() {
            {
                new MockUp<SalesStatisticsService>() {
                    @Mock
                    Object getAssignedDate(Map<String, Object> params) {
                        return putAssignedDateResult;
                    }
                };
            }
        };

        try {
            target.putAssignedDate("2020-02-10");
        } catch (DWArgumentException e) {
            assertTrue(e.getMessage().equals("Update Failed!"));
        }
    }


    @Test
    @DisplayName("get")
    void test_get_case() throws Exception {

        List<String> categoryIdList = new ArrayList<>();
        categoryIdList.add("app");

        List<String> goodsCodeList = new ArrayList<>();
        goodsCodeList.add("dop");

        List<String> strategyCodeList = new ArrayList<>();
        strategyCodeList.add("testStrategyCode");

        List<String> orderSourceList = new ArrayList<>();
        orderSourceList.add("tsetOrderSource");

        //場景1 : params{ALL}

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryIdList);
        params.put("goodsCode", goodsCodeList);
        params.put("strategyCode", strategyCodeList);
        params.put("orderSource", orderSourceList);
        params.put("startDate", "2020-02-01");
        params.put("endDate", "2020-02-10");

        Map<String, Object> permissionData = new HashMap<>();
        permissionData.put("Superadmin", false);
        permissionData.put("HaveData", true);
        permissionData.put("sql", "");
        permissionData.put("goodsCode", new ArrayList<>());
        permissionData.put("userPermission", "");

        String sql = "SELECT *FROM sales_statistics WHERE 1=1 AND null  ORDER BY payDate DESC LIMIT ?,?";

        List<Object> sqlParams = new ArrayList<Object>();
        sqlParams.add(0);
        sqlParams.add(10);

        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderSource", "Boss");
        resultMap.put("payDate", "2020/02/14 00:00:00");
        resultMap.put("categoryId", "app");
        resultMap.put("goodsCode", "DOP");
        resultMap.put("goodsName", "营运监控");
        resultMap.put("strategyCode", "DOP201811");
        resultMap.put("strategyName", "營運監控");
        resultMap.put("orderMode", 0);
        resultMap.put("orderCount", 1);
        resultMap.put("quantity", 1);
        resultMap.put("payPrice", 100);
        resultList.add(resultMap);

        new Expectations() {
            {
                mockDataPermissionService.getDataPermissionMap();
                result = permissionData;
                {
                    dao.select((DWDataSetOperationOption) any, anyString, new ArrayList<>().toArray());
                    result = resultList;

                    dao.select((DWDataSetOperationOption) any, anyString, sqlParams.toArray());
                    if (sql.startsWith("SELECT *FROM sales_statistics WHERE 1=1 AND null  ORDER")) {
                        result = resultList;
                    }
                }
            }
        };
        Map<String, Object> getResult = (Map<String, Object>) target.get(1, 10, params);
        List<Map<String, Object>> getList = (List<Map<String, Object>>) getResult.get("list");
        assertEquals(1, getResult.get("totalData"));
        assertEquals(1, getResult.get("totalPageNum"));
        assertEquals(true, getResult.get("success"));
        assertEquals("get success", getResult.get("message"));
        assertEquals(1, getList.size());

        //場景2 : params{only startDate}

        params.remove("endDate");

        getResult = (Map<String, Object>) target.get(1, 10, params);
        getList = (List<Map<String, Object>>) getResult.get("list");
        assertEquals(1, getResult.get("totalData"));
        assertEquals(1, getResult.get("totalPageNum"));
        assertEquals(true, getResult.get("success"));
        assertEquals("get success", getResult.get("message"));
        assertEquals(1, getList.size());

        //場景3 : params{only endDate}

        params.remove("startDate");
        params.put("endDate", "2020-02-10");

        getResult = (Map<String, Object>) target.get(1, 10, params);
        getList = (List<Map<String, Object>>) getResult.get("list");
        assertEquals(1, getResult.get("totalData"));
        assertEquals(1, getResult.get("totalPageNum"));
        assertEquals(true, getResult.get("success"));
        assertEquals("get success", getResult.get("message"));
        assertEquals(1, getList.size());

        //場景4 : 沒有配置數據權限

        permissionData.put("HaveData", false);

        getResult = (Map<String, Object>) target.get(1, 10, params);
        assertEquals("沒有配置數據權限資料", getResult.get("message"));
    }

    @Test
    @DisplayName("getOverallTotal")
    void test_getOverallTotal_case() throws Exception {

        //情境1 :  result=null

        List<String> categoryIdList = new ArrayList<>();
        categoryIdList.add("app");

        List<String> goodsCodeList = new ArrayList<>();
        goodsCodeList.add("dop");

        List<Object> sqlParams = new ArrayList<Object>();
        sqlParams.add(categoryIdList);
        sqlParams.add(goodsCodeList);

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryIdList);
        params.put("goodsCode", goodsCodeList);

        Map<String, Object> permissionData = new HashMap<>();
        permissionData.put("Superadmin", false);
        permissionData.put("HaveData", true);
        permissionData.put("sql", "");
        permissionData.put("goodsCode", new ArrayList<>());
        permissionData.put("userPermission", "");

        new Expectations() {
            {
                mockDataPermissionService.getDataPermissionMap();
                result = permissionData;

                dao.select((DWDataSetOperationOption) any, anyString, new ArrayList<>().toArray());
                result = new ArrayList<>();

            }
        };

        DWServiceResult overallTotalResult = (DWServiceResult) target.getOverallTotal(params);

        Map<String, Object> resultData = (Map<String, Object>) overallTotalResult.getData();

        assertTrue(overallTotalResult.getSuccess());
        assertEquals(0, resultData.get("totalPayPrice"));
        assertEquals(0, resultData.get("averageUnitPayPrice"));
        assertEquals(0, resultData.get("totalOrderCount"));

        //情境2 :  resultMapKey=null

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalOrderCount", null);
        resultMap.put("totalPayPrice", null);
        resultMap.put("averageUnitPayPrice", null);
        resultList.add(resultMap);

        new Expectations() {
            {
                dao.select((DWDataSetOperationOption) any, anyString, new ArrayList<>().toArray());
                result = resultList;
            }
        };
        overallTotalResult = (DWServiceResult) target.getOverallTotal(params);

        resultData = (Map<String, Object>) overallTotalResult.getData();

        assertTrue(overallTotalResult.getSuccess());
        assertEquals(0, resultData.get("totalPayPrice"));
        assertEquals(0, resultData.get("averageUnitPayPrice"));
        assertEquals(0, resultData.get("totalOrderCount"));


        //情境3 :  沒有配置數據權限資料

        permissionData.put("HaveData", false);
        overallTotalResult = (DWServiceResult) target.getOverallTotal(params);

        assertEquals("沒有配置數據權限資料", overallTotalResult.geMessage());
        assertTrue(overallTotalResult.getSuccess());
    }

    @Test
    @DisplayName("getMonthTotalDetails")
    void test_getMonthTotalDetails_case() throws Exception {

        //情境1 :  驗證正確資料、自動補齊日期

        List<Object> sqlParams = new ArrayList<Object>();
        String monthOfYear = "2020-02";
        sqlParams.add(monthOfYear + "%");
        List<String> categoryIdList = new ArrayList<>();
        categoryIdList.add("app");

        List<String> goodsCodeList = new ArrayList<>();
        goodsCodeList.add("dop");

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryIdList);
        params.put("goodsCode", goodsCodeList);

        Map<String, Object> permissionData = new HashMap<>();
        permissionData.put("Superadmin", false);
        permissionData.put("HaveData", true);
        permissionData.put("sql", "");
        permissionData.put("goodsCode", new ArrayList<>());
        permissionData.put("userPermission", "");

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("payDate", 10);
        resultMap.put("totalPayPrice", 100);
        resultMap.put("averageUnitPayPrice", 100);
        resultMap.put("totalQuantity", 1);
        resultMap.put("totalOrderCount", 1);
        resultList.add(resultMap);

        new Expectations() {
            {
                mockDataPermissionService.getDataPermissionMap();
                result = permissionData;

                dao.select((DWDataSetOperationOption) any, anyString, sqlParams.toArray());
                result = resultList;
            }
        };

        DWServiceResult MonthTotalDetailsResult = (DWServiceResult) target.getMonthTotalDetails(monthOfYear, params);
        List<Map<String, Object>> MonthTotalDetailList = (List<Map<String, Object>>) MonthTotalDetailsResult.getData();
        Map<String, Object> MonthTotalDetailMap = MonthTotalDetailList.get(0);
        Map<String, Object> MonthTotalDetailMap2 = MonthTotalDetailList.get(9);

        assertEquals(0, MonthTotalDetailMap.get("totalQuantity"));
        assertEquals(0, MonthTotalDetailMap.get("averageUnitPayPrice"));
        assertEquals(0, MonthTotalDetailMap.get("totalOrderCount"));
        assertEquals(0, MonthTotalDetailMap.get("totalPayPrice"));

        assertEquals(1, MonthTotalDetailMap2.get("totalQuantity"));
        assertEquals(100, MonthTotalDetailMap2.get("averageUnitPayPrice"));
        assertEquals(1, MonthTotalDetailMap2.get("totalOrderCount"));
        assertEquals(100, MonthTotalDetailMap2.get("totalPayPrice"));
        assertTrue(MonthTotalDetailsResult.getSuccess());

        //情境2 :  沒有配置數據權限資料
        permissionData.put("HaveData", false);
        List<Map<String, Object>> resultList2 = new ArrayList<>();
        resultMap = new HashMap<>();
        resultMap.put("payDate", 10);
        resultMap.put("totalPayPrice", 100);
        resultMap.put("averageUnitPayPrice", 100);
        resultMap.put("totalQuantity", 1);
        resultMap.put("totalOrderCount", 1);
        resultList2.add(resultMap);

        new Expectations() {
            {
                dao.select((DWDataSetOperationOption) any, anyString, sqlParams.toArray());
                result = resultList2;
            }
        };
        MonthTotalDetailsResult = (DWServiceResult) target.getMonthTotalDetails(monthOfYear, params);

        assertEquals("沒有配置數據權限資料", MonthTotalDetailsResult.geMessage());
        assertTrue(MonthTotalDetailsResult.getSuccess());

        //情境3 :  日期格式錯誤
        permissionData.put("HaveData", true);
        monthOfYear = "2020-022";

        MonthTotalDetailsResult = (DWServiceResult) target.getMonthTotalDetails(monthOfYear, params);

        assertEquals("請輸入正確的年月格式 -> yyyy-MM", MonthTotalDetailsResult.geMessage());
        assertTrue(MonthTotalDetailsResult.getSuccess());
    }


    @Test
    @DisplayName("getYearTotalDetails")
    void test_getYearTotalDetails_case() throws Exception {

        //情境1 :  驗證正確資料、自動補齊日期

        List<Object> sqlParams = new ArrayList<Object>();
        String year = "2020";
        sqlParams.add(year + "%");
        List<String> categoryIdList = new ArrayList<>();
        categoryIdList.add("app");

        List<String> goodsCodeList = new ArrayList<>();
        goodsCodeList.add("dop");

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryIdList);
        params.put("goodsCode", goodsCodeList);

        Map<String, Object> permissionData = new HashMap<>();
        permissionData.put("Superadmin", false);
        permissionData.put("HaveData", true);
        permissionData.put("sql", "");
        permissionData.put("goodsCode", new ArrayList<>());
        permissionData.put("userPermission", "");

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("payDate", 10);
        resultMap.put("totalPayPrice", 100);
        resultMap.put("averageUnitPayPrice", 100);
        resultMap.put("totalQuantity", 1);
        resultMap.put("totalOrderCount", 1);
        resultList.add(resultMap);

        new Expectations() {
            {
                mockDataPermissionService.getDataPermissionMap();
                result = permissionData;

                dao.select((DWDataSetOperationOption) any, anyString, sqlParams.toArray());
                result = resultList;
            }
        };

        DWServiceResult YearTotalDetailsResult = (DWServiceResult) target.getYearTotalDetails(year, params);
        List<Map<String, Object>> YearTotalDetailsList = (List<Map<String, Object>>) YearTotalDetailsResult.getData();
        Map<String, Object> YearTotalDetailsMap = YearTotalDetailsList.get(0);
        Map<String, Object> YearTotalDetailsMap2 = YearTotalDetailsList.get(9);

        assertEquals(0, YearTotalDetailsMap.get("totalQuantity"));
        assertEquals(0, YearTotalDetailsMap.get("averageUnitPayPrice"));
        assertEquals(0, YearTotalDetailsMap.get("totalOrderCount"));
        assertEquals(0, YearTotalDetailsMap.get("totalPayPrice"));

        assertEquals(1, YearTotalDetailsMap2.get("totalQuantity"));
        assertEquals(100, YearTotalDetailsMap2.get("averageUnitPayPrice"));
        assertEquals(1, YearTotalDetailsMap2.get("totalOrderCount"));
        assertEquals(100, YearTotalDetailsMap2.get("totalPayPrice"));
        assertTrue(YearTotalDetailsResult.getSuccess());

        //情境2 :  沒有配置數據權限資料

        permissionData.put("HaveData", false);
        List<Map<String, Object>> resultList2 = new ArrayList<>();
        resultMap = new HashMap<>();
        resultMap.put("payDate", 10);
        resultMap.put("totalPayPrice", 100);
        resultMap.put("averageUnitPayPrice", 100);
        resultMap.put("totalQuantity", 1);
        resultMap.put("totalOrderCount", 1);
        resultList2.add(resultMap);

        new Expectations() {
            {
                dao.select((DWDataSetOperationOption) any, anyString, sqlParams.toArray());
                result = resultList2;
            }
        };
        YearTotalDetailsResult = (DWServiceResult) target.getYearTotalDetails(year, params);

        assertEquals("沒有配置數據權限資料", YearTotalDetailsResult.geMessage());
        assertTrue(YearTotalDetailsResult.getSuccess());

        //情境3 :  日期格式錯誤

        permissionData.put("HaveData", true);
        year = "20202";

        YearTotalDetailsResult = (DWServiceResult) target.getYearTotalDetails(year, params);

        assertEquals("請輸入正確的年格式 -> yyyy", YearTotalDetailsResult.geMessage());
        assertTrue(YearTotalDetailsResult.getSuccess());
    }

    @Test
    @DisplayName("getSalesRank")
    void test_getSalesRank_case() throws Exception {

        //情境1 :  驗證正確資料

        String field = "payPrice";
        String sql = "SELECT goodsName,strategyName,SUM(quantity) AS quantity,SUM(payPrice) AS payPrice FROM sales_statistics WHERE 1 = 1 AND orderMode = 1 AND null GROUP BY strategyCode ORDER by payPrice DESC LIMIT 0, 10";
        String totalSql = "SELECT SUM(payPrice) AS total FROM sales_statistics WHERE 1 = 1 AND orderMode = 1  AND null";

        List<Object> sqlParams = new ArrayList<Object>();

        List<String> categoryIdList = new ArrayList<>();
        categoryIdList.add("app");

        List<String> goodsCodeList = new ArrayList<>();
        goodsCodeList.add("dop");

        Map<String, Object> params = new HashMap<>();
        params.put("rank", "10");
        params.put("categoryId", categoryIdList);
        params.put("goodsCode", goodsCodeList);
        params.put("startDate", "2020-02-01");
        params.put("endDate", "2020-02-10");


        Map<String, Object> permissionData = new HashMap<>();
        permissionData.put("Superadmin", false);
        permissionData.put("HaveData", true);
        permissionData.put("sql", "");
        permissionData.put("goodsCode", new ArrayList<>());
        permissionData.put("userPermission", "");

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("goodsName", "营运监控");
        resultMap.put("strategyName", "營運監控");
        resultMap.put("quantity", 7);
        resultMap.put("payPrice", 700);
        resultList.add(resultMap);

        List<Map<String, Object>> totalList = new ArrayList<>();
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("total", 700);
        totalList.add(totalMap);

        new Expectations() {
            {
                {
                    mockDataPermissionService.getDataPermissionMap();
                    result = permissionData;
                }
                {
                    dao.select((DWDataSetOperationOption) any, sql, sqlParams.toArray());
                    if (sql.startsWith("SELECT goodsName,strategyName,SUM(quantity) AS quantity,SUM(payPrice) AS payPrice FROM sales_statistics WHERE 1 = 1 AND orderMode = 1 AND null GROUP BY strategyCode ORDER by payPrice DESC LIMIT 0, 10")) {
                        result = resultList;
                    }
                    dao.select((DWDataSetOperationOption) any, totalSql, sqlParams.toArray());
                    if (totalSql.equals("SELECT SUM(payPrice) AS total FROM sales_statistics WHERE 1 = 1 AND orderMode = 1  AND null")) {
                        result = totalList;
                    }
                }
            }
        };
        DWServiceResult SalesRankResult = (DWServiceResult) target.getSalesRank(field, params);

        List<Map<String, Object>> SalesRankList = (List<Map<String, Object>>) SalesRankResult.getData();
        Map<String, Object> SalesRankMap = SalesRankList.get(0);

        assertEquals("营运监控", SalesRankMap.get("goodsName"));
        assertEquals("營運監控", SalesRankMap.get("strategyName"));
        assertEquals(7, SalesRankMap.get("quantity"));
        assertEquals(700, SalesRankMap.get("payPrice"));
        assertEquals(100.0, SalesRankMap.get("percentage"));
        assertTrue(SalesRankResult.getSuccess());

        //情境2:  Only startDate
        params.remove("endDate");
        SalesRankResult = (DWServiceResult) target.getSalesRank(field, params);

        SalesRankList = (List<Map<String, Object>>) SalesRankResult.getData();
        SalesRankMap = SalesRankList.get(0);

        assertEquals("营运监控", SalesRankMap.get("goodsName"));
        assertEquals("營運監控", SalesRankMap.get("strategyName"));
        assertEquals(7, SalesRankMap.get("quantity"));
        assertEquals(700, SalesRankMap.get("payPrice"));
        assertEquals(100.0, SalesRankMap.get("percentage"));
        assertTrue(SalesRankResult.getSuccess());

        //情境3:  Only endDate
        params.put("endDate", "2020-02-10");
        params.remove("startDate");
        SalesRankResult = (DWServiceResult) target.getSalesRank(field, params);

        SalesRankList = (List<Map<String, Object>>) SalesRankResult.getData();
        SalesRankMap = SalesRankList.get(0);

        assertEquals("营运监控", SalesRankMap.get("goodsName"));
        assertEquals("營運監控", SalesRankMap.get("strategyName"));
        assertEquals(7, SalesRankMap.get("quantity"));
        assertEquals(700, SalesRankMap.get("payPrice"));
        assertEquals(100.0, SalesRankMap.get("percentage"));
        assertTrue(SalesRankResult.getSuccess());

        //情境4 :  沒有配置數據權限資料

        permissionData.put("HaveData", false);
        SalesRankResult = (DWServiceResult) target.getSalesRank(field, params);

        assertEquals("沒有配置數據權限資料", SalesRankResult.geMessage());
        assertTrue(SalesRankResult.getSuccess());
    }
}