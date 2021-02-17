package com.digiwin.boss.dwreport.service;

import com.digiwin.app.service.DWService;

import java.util.Map;

/**
 * @author Miko
 */
public interface ISalesStatisticsHistoryService extends DWService {
    public Object post(Map<String, Object> params) throws Exception;
    public Object get(int pageNum, int pageSize) throws Exception;

    }
