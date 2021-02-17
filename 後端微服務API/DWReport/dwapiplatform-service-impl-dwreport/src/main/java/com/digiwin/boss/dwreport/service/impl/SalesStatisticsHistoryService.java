package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.boss.dwreport.service.ISalesStatisticsHistoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public class SalesStatisticsHistoryService implements ISalesStatisticsHistoryService {
    @Autowired
    @Qualifier("bossDao")
    private DWDao dao;

    private static final Log log = LogFactory.getLog(SalesStatisticsHistoryService.class);

    // 資料表
    static final String CHANGE_HISTORY = "change_history";
    //欄位名稱
    static final String UPDATE_DATE = "updateDate";
    static final String UPDATE_STATUS = "updateStatus";
    static final String UPDATE_COUNT = "updateCount";
    static final String UPDATE_ID = "updateID";
    static final String UPDATE_NAME = "updateName";
    //SQL語句
    static final String INSERT_SQL = "INSERT INTO change_history (updateDate, updateStatus, updateCount, updateID, updateName) values(?, ?, ?, ?, ?)";
    static final String SELECT_SQL = "SELECT * FROM change_history ORDER BY createDate DESC";

    @Override
    public Object post(Map<String, Object> params) throws Exception {
        DWQueryInfo queryInfo = new DWQueryInfo();
        queryInfo.setTableName(CHANGE_HISTORY);
        int status;
        String date = String.valueOf(params.getOrDefault(UPDATE_DATE, ""));
        if (params.containsKey(UPDATE_STATUS)) {
            status = (int)params.get(UPDATE_STATUS);
        } else {
            throw new DWArgumentException("status", "status is null or empty!");

        }

        int count;
        if (params.containsKey(UPDATE_COUNT)) {
            count = (int)params.get(UPDATE_COUNT);
        } else {
            throw new DWArgumentException("count", "count cannot be minus!");

        }


//        int count = String.valueOf((String) params.getOrDefault(UPDATE_COUNT, 0));
        String id = String.valueOf(params.getOrDefault(UPDATE_ID, ""));
        String name = String.valueOf(params.getOrDefault(UPDATE_NAME, ""));


        if (date == null || date.isEmpty()) {
            throw new DWArgumentException("date", "date is null or empty!");
        }
        if (status != 0.0 && status != 1.0) {
            throw new DWArgumentException("status", "status must be 0 or 1!");
        }
        if (count < 0.0) {
            throw new DWArgumentException("count", "count cannot be minus!");
        }
        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }
        if (name == null || name.isEmpty()) {
            throw new DWArgumentException("name", "name is null or empty!");
        }

        String updateDate = params.get(UPDATE_DATE).toString();
        int updateStatus = (int)params.get(UPDATE_STATUS);
        int updateCount = (int)params.get(UPDATE_COUNT);
        String updateID = params.get(UPDATE_ID).toString();
        String updateName = params.get(UPDATE_NAME).toString();

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = dao.update(option, INSERT_SQL, updateDate, updateStatus, updateCount,
                updateID, updateName);

        return DWServiceResultBuilder.build("新增成功", result);
    }

    @Override
    public Object get(int pageNum, int pageSize) throws Exception {

        DWPagableQueryInfo pagableQueryInfo = new DWPagableQueryInfo();
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);
        pagableQueryInfo.setPageSize(pageSize);
        pagableQueryInfo.setPageNumber(pageNum);

        Object result = dao.selectWithPage(pagableQueryInfo, SELECT_SQL, option);

        return result;
    }
}
