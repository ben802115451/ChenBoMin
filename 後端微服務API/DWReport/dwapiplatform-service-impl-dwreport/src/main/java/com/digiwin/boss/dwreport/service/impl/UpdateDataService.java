package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.boss.dwreport.service.IUpdateDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Miko
 */

public class UpdateDataService implements IUpdateDataService {
    @Autowired
    private SalesStatisticsService salesStatisticsService;

    @Override
    public Object put(int year, int month) throws Exception {
        if (month == 0 || month > 12) {
            throw new DWArgumentException("month", "您指定的月份為：" + month + " 請輸入合法的月份！");
        }

        Calendar c = Calendar.getInstance();
        for (int i = month - 1; i < month; i++) {
            c.set(year, i, 1);
            int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int x = 1; x <= lastDay; x++) {
                String date = year + "-" + (i + 1) + "-" + x;
                salesStatisticsService.putAssignedDate(date);
            }
        }
        return "OK";
    }
}
