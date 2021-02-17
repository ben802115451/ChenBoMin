package com.digiwin.dwapiplatform.dwsysmanagement.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public interface IRelesNoteService extends DWService {

    /**
     * 新增公告
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 修改公告
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(DWDataSet dataset) throws Exception;

    /**
     * 取得有效的公告
     *
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object getActiveList() throws Exception;

    /**
     * 取得公告列表
     *
     * @param queryInfo
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    /**
     * @param oids
     * @return
     * @throws Exception
     */
    public Object getDetails(List<Object> oids) throws Exception;

    /**
     * 刪除公告
     *
     * @return
     * @throws Exception
     */
    public Object delete(List<Object> oids) throws Exception;

//    /**
//     * @param oids
//     * @return
//     * @throws Exception
//     */
//    public Object deleteDetails(List<Object> oids) throws Exception;

    /**
     * 將公告上架
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putActive(String oid) throws Exception;

    /**
     * 將公告下架
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putInactive(String oid) throws Exception;
}
