package com.digiwin.developer.dwdeveloper.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.DWService;

import java.util.List;

/**
 * @author Miko
 */
public interface IFileManagementService extends DWService {
    /**
     * 新增文檔
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 修改文檔
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(DWDataSet dataset) throws Exception;

    /**
     * 刪除文檔
     * @param id
     * @return
     * @throws Exception
     */
    public Object delete(String id) throws Exception;

    /**
     * 取得文檔清單
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    /**
     * 取得文檔詳細清單
     * @return
     * @throws Exception
     */
    public Object getDetailList(String id) throws Exception;


    /**
     * 商品上架
     * @param id
     * @return
     * @throws Exception
     */
    public Object putActive(String id) throws Exception;

    /**
     * 商品下架
     * @param id
     * @return
     * @throws Exception
     */
    public Object putInactive(String id) throws Exception;

}
