package com.digiwin.marketmanagement.dwmarketmanagement.service;

import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

public interface IMediaAreaService extends DWService {

    /**
     * 新增媒體播放區
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 查詢媒體播放區
     *
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object get() throws Exception;
}
