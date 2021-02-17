package com.digiwin.developer.dwdeveloper.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.DWService;

import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public interface IDownloadCenterService extends DWService {
    /**
     * 取得下載清單
     * @param queryInfo
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;
}
