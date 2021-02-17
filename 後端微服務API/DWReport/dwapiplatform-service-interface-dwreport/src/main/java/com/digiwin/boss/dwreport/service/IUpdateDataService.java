package com.digiwin.boss.dwreport.service;

import com.digiwin.app.service.DWService;

/**
 * @author Miko
 */
public interface IUpdateDataService extends DWService {
    public Object put(int year, int month) throws Exception;
}
