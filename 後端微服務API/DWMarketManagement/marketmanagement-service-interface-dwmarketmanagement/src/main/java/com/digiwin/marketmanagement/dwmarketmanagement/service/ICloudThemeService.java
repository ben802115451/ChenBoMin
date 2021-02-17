package com.digiwin.marketmanagement.dwmarketmanagement.service;

import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

import java.util.List;

/**
 * @author Miko
 */
public interface ICloudThemeService extends DWService {
    /**
     * 新增主題
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 修改主題
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(DWDataSet dataset) throws Exception;

    /**
     * 刪除主題
     * @param oids
     * @return
     * @throws Exception
     */
    public Object delete(List<Object> oids) throws Exception;

    /**
     * 取得5筆上架資訊
     * @return
     * @throws Exception
     */
     @AllowAnonymous
    public Object getActiveList() throws Exception;

    /**
     * 取得所有下架資訊
     * @return
     * @throws Exception
     */
    public Object getInactiveList() throws Exception;

    /**
     * 主題上架(最多5筆上架)
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putActive(String oid) throws Exception;

    /**
     * 主題下架
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putInactive(String oid) throws Exception;


    /**
     * 新增商品
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object postGoods(DWDataSet dataset) throws Exception;

    /**
     * 取得商品列表
     * @param oid
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object getGoodsList(String oid) throws Exception;
}
