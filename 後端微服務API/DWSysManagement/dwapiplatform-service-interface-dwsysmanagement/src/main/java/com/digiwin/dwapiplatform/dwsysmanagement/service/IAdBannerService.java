package com.digiwin.dwapiplatform.dwsysmanagement.service;


import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;


/**
 * 廣告圖檔管理
 *
 * @author yuge77
 */
@Deprecated
public interface IAdBannerService extends DWService {

    // 取得列表(單頭)
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    // 新增資訊
    public Object post(DWDataSet dataset) throws Exception;

    // 修改產品線佈署訊
    public Object put(DWDataSet dataset) throws Exception;

    // 刪除佈署
    public Object delete(DWDataSet dataset) throws Exception;

    /*
     * public Object getChangeHistory(Map<String, Object> param) throws Exception;
     */

    // 取得廣告區塊清單
    public Object getBlocks(DWQueryInfo queryInfo) throws Exception;

    // 取得排序清單
    @AllowAnonymous
    public Object getRotateList(DWQueryInfo queryInfo) throws Exception;
}
