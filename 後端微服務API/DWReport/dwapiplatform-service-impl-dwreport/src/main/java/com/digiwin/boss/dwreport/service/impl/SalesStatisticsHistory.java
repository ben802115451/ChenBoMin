package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.DWContainerContext;
import com.digiwin.app.container.DWDefaultParameters;
import com.digiwin.app.container.exceptions.DWArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public class SalesStatisticsHistory {

    @Autowired
    private DWContainerContext containerContext;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = DWArgumentException.class)
    public Object postSalesStatisticsHistory(DWDefaultParameters parameters, Map<String, Object> profile) throws Exception {

        Object invoke = containerContext.invoke("DWReport", "ISalesStatisticsHistoryService", "post", parameters, profile);
        return invoke;
    }
}
