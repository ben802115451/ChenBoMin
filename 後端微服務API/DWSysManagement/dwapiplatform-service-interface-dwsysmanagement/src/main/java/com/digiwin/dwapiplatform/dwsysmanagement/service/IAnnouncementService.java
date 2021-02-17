package com.digiwin.dwapiplatform.dwsysmanagement.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

import java.util.Map;

/**
 * @author Miko
 */
public interface IAnnouncementService extends DWService {

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
     * 取得顯示的公告
     *
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object getActiveAnnouncement(String goodsCode, String displayId) throws Exception;

    /**
     * 取得公告列表
     *
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    /**
     * 刪除公告
     *
     * @return
     * @throws Exception
     */
    public Object delete(String id) throws Exception;

    /**
     * 將公告上架
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Object putActive(String id) throws Exception;

    /**
     * 將公告下架
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Object putInactive(String id) throws Exception;

    /**
     * 新增頁面
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object postDisplayPage(DWDataSet dataset) throws Exception;

    /**
     * 修改顯示名稱
     * @param displayName
     * @param pageId
     * @return
     * @throws Exception
     */
    public Object putDisplayName(String displayName, String pageId) throws Exception;

    /**刪除頁面
     *
     * @param pageId
     * @return
     * @throws Exception
     */
    public Object deleteDisplayPage(String pageId) throws Exception;


    public Object getDisplayPageList(String goodsCode) throws Exception;


}
