package com.digiwin.boss.dwreport.service;

import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.DWService;

/**
 * @author Miko
 */
public interface IExcludeOrderService extends DWService {
    /**
     * 新增欲排除的訂單
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 修改欲排除的訂單
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(DWDataSet dataset) throws Exception;

    /**
     * 刪除欲排除的訂單
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object delete(DWDataSet dataset) throws Exception;

    /**
     * 取得欲排除的訂單
     * @return
     * @throws Exception
     */
    public Object get() throws Exception;
}
