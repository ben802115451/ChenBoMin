package com.digiwin.boss.dwreport.service;

import com.digiwin.app.service.DWService;

import java.util.List;
import java.util.Map;

public interface IOrderService extends DWService {

    //查詢訂單明細報表
    /**
     *
     * @param pageNum 第幾頁
     * @param pageSize 一頁幾筆
     * @param params 查詢參數
     * @return
     * @throws Exception
     */
    public Object getList(int pageNum, int pageSize, Map<String, Object> params) throws Exception;

    //輸出EXCEL報表
    /**
     *
     * @param params 查詢參數
     * @return
     * @throws Exception
     */
    public Object getExcel(Map<String, Object> params) throws Exception;

    //查詢客戶到期報表
    /**
     *
     * @param pageNum
     * @param pageSize
     * @param params
     * @return
     * @throws Exception
     */
    public Object getExpirationList(int pageNum, int pageSize, Map<String, Object> params) throws Exception;

    //輸出EXCEL到期報表
    /**
     *
     * @param params
     * @return
     * @throws Exception
     */
    public Object getExpirationExcel(Map<String, Object> params) throws Exception;
}
