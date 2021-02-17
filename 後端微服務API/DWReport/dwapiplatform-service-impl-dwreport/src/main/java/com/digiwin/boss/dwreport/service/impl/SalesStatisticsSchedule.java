package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.DWContainerContext;
import com.digiwin.app.container.DWDefaultParameters;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.schedule.entity.DWJobResult;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.boss.dwreport.service.ISalesStatisticsScheduleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Miko
 */
public class SalesStatisticsSchedule implements ISalesStatisticsScheduleService {

    @Autowired
    private DWContainerContext containerContext;

    @Override
    public DWJobResult executeJob(Map<String, Object> paramMap) throws Exception {

        DWJobResult result = new DWJobResult();
        result.setExecuteStatus(DWJobResult.OK);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        Map<String, Object> map = new HashMap<>();

        map.put("payDate", yesterday);

        String iamUserId = DWModuleConfigUtils.getCurrentModuleProperty("iamUserId");
        String iamPassword = DWModuleConfigUtils.getCurrentModuleProperty("iamPassword");
        Map<String, Object> iamResultMap = IamLogin.sendPost(iamUserId, iamPassword);
        String userToken = iamResultMap.get("token").toString();
        String userId = iamResultMap.get("userId").toString();
        String userName = iamResultMap.get("userName").toString();

        DWServiceContext context = DWServiceContext.getContext();
        Map<String, Object> requestHeader = context.getRequestHeader();
        requestHeader.put("token", userToken);

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        profile.put("userId", userId);
        profile.put("userName", "系統更新");

        DWDefaultParameters parameters = new DWDefaultParameters();
        parameters.put("date", yesterday);

        containerContext.invoke("DWReport", "ISalesStatisticsService", "putAssignedDate", parameters, profile);

        return result;

    }
}
