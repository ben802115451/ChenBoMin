package com.digiwin.marketmanagement.dwmarketmanagement.service;

import com.digiwin.app.service.DWService;

import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public interface IGoodsTypeService extends DWService {

    /**
     * 查詢商品類型
     *
     * @param oids
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> get(List<String> oids) throws Exception;
}
