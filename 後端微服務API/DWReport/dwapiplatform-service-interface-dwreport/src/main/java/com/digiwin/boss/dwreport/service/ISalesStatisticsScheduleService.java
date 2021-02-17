package com.digiwin.boss.dwreport.service;

import com.digiwin.app.schedule.entity.DWJobResult;
import com.digiwin.app.schedule.quartz.job.DWJob;
import com.digiwin.app.service.DWService;

import java.util.Map;

/**
 * @author Miko
 */
public interface ISalesStatisticsScheduleService extends DWJob, DWService {
    public DWJobResult executeJob(Map<String, Object> paramMap) throws Exception;
}
