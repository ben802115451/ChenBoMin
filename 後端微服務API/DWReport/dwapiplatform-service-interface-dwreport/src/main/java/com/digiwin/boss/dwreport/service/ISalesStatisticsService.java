package com.digiwin.boss.dwreport.service;

import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.DWService;

import java.util.Map;

public interface ISalesStatisticsService extends DWService {

    /**
     * @param params 查詢參數
     * @return
     * @throws Exception
     */
    public Object getAssignedDate(Map<String, Object> params) throws Exception;

    /**
     * @param pageNum  第幾頁
     * @param pageSize 一頁幾筆
     * @param params   查詢參數 {"categoryId" : [],"goodsCode":[],"strategyCode":[],"StartDate":"2019-10-28","EndDate":"2019-12-13","orderSource":[]}
     * @return
     * @throws Exception
     */
    public Object get(int pageNum, int pageSize, Map<String, Object> params) throws Exception;

    /**
     * @param date 日期 例:2019-10-28
     * @return
     * @throws Exception
     */
    public Object putAssignedDate(String date) throws Exception;

    /**
     * @param params { "CategoryId" , "GoodsCode"}
     * @return
     * @throws Exception
     */
    public Object getOverallTotal(Map<String, Object> params) throws Exception;

    /**
     * @param monthOfYear  指定月份 例:2020-01
     * @param params { "CategoryId" , "GoodsCode"}
     * @return
     * @throws Exception
     */
    public Object getMonthTotalDetails(String monthOfYear, Map<String, Object> params) throws Exception;

    /**
     * @param year   指定年份 例:2020
     * @param params { "CategoryId" , "GoodsCode"}
     * @return
     * @throws Exception
     */
    public Object getYearTotalDetails(String year, Map<String, Object> params) throws Exception;

    /**
     * @param field  payPrice(銷售金額) 或 quantity(銷售數量)
     * @param params {"rank":"10","CategoryId":[""] , "GoodsCode":[""],"startDate":"2020-02-01","endDate":"2020-02-02"}
     * @return
     * @throws Exception
     */
    public Object getSalesRank(String field, Map<String, Object> params) throws Exception;

    /**
     * 取得今日銷售統計數據總計服務
     *
     * @param params
     * @return
     * @throws Exception
     */
    public Object getTodayTotal(Map<String, Object> params) throws Exception;

    /**
     * 取得前七日銷售統計數據總計明細服務
     *
     * @param params
     * @return
     * @throws Exception
     */
    public Object getPreviousSevenDaysTotalDetails(Map<String, Object> params) throws Exception;

    /**
     * 取得指定月份銷售統計數據總計服務
     *
     * @param params
     * @return
     * @throws Exception
     */

    public Object getMonthTotal(String monthOfYear, Map<String, Object> params) throws Exception;

    /**
     * 取得指定年份銷售統計數據總計服務
     *
     * @param params
     * @return
     * @throws Exception
     */
    public Object getYearTotal(String year, Map<String, Object> params) throws Exception;
}
