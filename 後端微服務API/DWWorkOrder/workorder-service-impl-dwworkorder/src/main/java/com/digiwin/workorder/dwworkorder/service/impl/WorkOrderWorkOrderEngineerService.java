package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.workorder.dwworkorder.service.IWorkOrderEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * @author Miko
 */
public class WorkOrderWorkOrderEngineerService implements IWorkOrderEngineerService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    @Override
    public Object post(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_ENGINEER);
        String workOrderTypeId = "";
        String workOrderEngineerName = "";
        String workOrderEngineerId = "";
        String email = "";
        String startTime = "";
        String endTime = "";

        for (DWDataRow row : table.getRows()) {
            workOrderTypeId = (String) row.get(DBConstants.WORK_TYPE_ID);
            if (workOrderTypeId == null || workOrderTypeId.isEmpty()) {
                throw new DWArgumentException("workOrderTypeId", "workOrderTypeId is null or empty!");
            }

            workOrderEngineerName = (String) row.get(DBConstants.ENGINEER_NAME);
            if (workOrderEngineerName == null || workOrderEngineerName.isEmpty()) {
                throw new DWArgumentException("workOrderEngineerName", "workOrderEngineerName is null or empty!");
            }

            workOrderEngineerId = (String) row.get(DBConstants.ENGINEER_ID);
            if (workOrderEngineerId == null || workOrderEngineerId.isEmpty()) {
                throw new DWArgumentException("workOrderEngineerId", "workOrderEngineerId is null or empty!");
            }

            email = (String) row.get(DBConstants.MAIL);
            if (email == null || email.isEmpty()) {
                throw new DWArgumentException("email", "email is null or empty!");
            }

            startTime = (String) row.get(DBConstants.START_TIME);
            if (startTime == null || startTime.isEmpty()) {
                throw new DWArgumentException("startTime", "startTime is null or empty!");
            }

            endTime = (String) row.get(DBConstants.END_TIME);
            if (endTime == null || endTime.isEmpty()) {
                throw new DWArgumentException("endTime", "endTime is null or empty!");
            }

            List<Map<String, Object>> result = this.dao.select(option, DBConstants.SELECT_FROM_WORK_ORDER_ENGINEER, workOrderTypeId);
            Object longcount = result.get(0).get("count('isPrincipal')");
            //某個工單類型，第一次新增的人員要為主要負責人
            String count = longcount.toString();
            if (count.equals("0")) {
                row.set(DBConstants.IS_PRINCIPAL, 1);
            } else {
                row.set(DBConstants.IS_PRINCIPAL, 0);
            }
        }
        Object result = this.dao.execute(dataset);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object put(DWDataSet dataset) throws Exception {
        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_ENGINEER);
        String workOrderEngineerName = "";
        String workOrderEngineerId = "";
        String start_time = "";
        String end_time = "";

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        for (DWDataRow row : table.getRows()) {
            workOrderEngineerName = (String) row.get(DBConstants.ENGINEER_NAME);
            if (workOrderEngineerName == null || workOrderEngineerName.isEmpty()) {
                throw new DWArgumentException("workOrderEngineerName", "workOrderEngineerName is null or empty!");
            }

            workOrderEngineerId = (String) row.get(DBConstants.ENGINEER_ID);
            if (workOrderEngineerId == null || workOrderEngineerId.isEmpty()) {
                throw new DWArgumentException("workOrderEngineerId", "workOrderEngineerId is null or empty!");
            }

            start_time = (String) row.get(DBConstants.START_TIME);
            if (start_time == null || start_time.isEmpty()) {
                throw new DWArgumentException("start_time", "start_time is null or empty!");
            }
            end_time = (String) row.get(DBConstants.END_TIME);
            if (end_time == null || end_time.isEmpty()) {
                throw new DWArgumentException("end_time", "end_time is null or empty!");
            }
        }
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object delete(String id, String type_id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DBConstants.ENGINEER_ID, id);
        map.put(DBConstants.WORK_TYPE_ID, type_id);
        map.put("$state", "D");
        DWDataSet dataSet = new DWDataSet();
        dataSet.newTable(DBConstants.WORK_ORDER_ENGINEER).newRow(map);
        DWSQLExecutionResult result = this.dao.execute(dataSet, option);

        return DWServiceResultBuilder.build(result);
    }

    //不用寫這個api，因為在工單類型的服務裡面有一個取單身的api，就可以取得該工單類型底下的工程人員清單
//    @Override
//    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
//        return null;
//    }

    @Override
    public Object putIsPrincipal(String id, String type_id) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        if (type_id == null || type_id.isEmpty()) {
            throw new DWArgumentException("type_id", "type_id is null or empty!");
        }

        DWQueryInfo queryInfo = new DWQueryInfo(DBConstants.WORK_ORDER_ENGINEER)
                .addEqualInfo(DBConstants.WORK_TYPE_ID, type_id)
                .addEqualInfo(DBConstants.ENGINEER_ID, id);

        DWDataSet dataSet = this.dao.select(queryInfo, option);
        if (dataSet.getTable(DBConstants.WORK_ORDER_ENGINEER).getRows().size() == 0) {
            throw new DWBusinessException(String.format("找不到指定的工程人員 id = %s", id));
        }

        //先把指定的工單類型下的isPrincipal設成0
        this.dao.update(option, DBConstants.UPDATE_TO_UNPRINCIPAL, type_id);
        //再把入參指定的工單類型id和工程人員id的isPrincipal設成1
        Object result = this.dao.update(option, DBConstants.UPDATE_TO_PRINCIPAL, type_id, id);

        return DWServiceResultBuilder.build(result);
    }
}
