package com.digiwin.marketmanagement.dwmarketmanagement.service;

import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

public interface IRecommendedProductsService extends DWService {

    /**
     * 新增推薦商品
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(DWDataSet dataset) throws Exception;

    /**
     * 查詢推薦商品
     *
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object get() throws Exception;
}
