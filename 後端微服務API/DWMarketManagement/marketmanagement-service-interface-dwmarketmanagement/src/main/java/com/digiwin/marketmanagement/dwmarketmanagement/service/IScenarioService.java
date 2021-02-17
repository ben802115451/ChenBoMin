package com.digiwin.marketmanagement.dwmarketmanagement.service;

import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

/**
 * @author Miko
 */
public interface IScenarioService extends DWService {
    /**
     * 新增情境圖
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 查詢情境圖
     * @param areaType
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object get(String areaType) throws Exception;

    /**
     * 修改情境圖
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(DWDataSet dataset) throws Exception;

}
