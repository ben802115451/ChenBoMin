package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.workorder.dwworkorder.service.IWorkOrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Miko
 */
public class WorkOrderTypeService implements IWorkOrderTypeService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;


    static final String SQL_TITLE = "SELECT t.service_type_id,s.service_name, t.type_id,t.type_name, t.sequence,t.create_by,t.create_by_id\n" +
            " ,t.create_date,t.modify_by,t.modify_by_id,t.modify_date ,t.create_by_userId FROM work_order_service_type AS s , work_order_type AS t\n" +
            " LEFT JOIN work_order_engineer AS e on  t.type_id = e.type_id WHERE t.service_type_id = s.service_id ";

    static final String GROUP_AND_ORDER = " GROUP BY t.type_id ORDER BY s.service_id , t.type_id";

    @Override
    public Object post(DWDataSet dataset) throws Exception {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        String create_by_userId = (String) profile.get("userId");

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_TYPE);
        String serviceTypeId = "";
        String workOrderTypeName = "";
        for (DWDataRow row : table.getRows()) {
            serviceTypeId = (String) row.get(DBConstants.SERVICE_TYPE_ID);
            if (serviceTypeId == null || serviceTypeId.isEmpty()) {
                throw new DWArgumentException("serviceTypeId", "serviceTypeId is null or empty!");
            }

            workOrderTypeName = (String) row.get(DBConstants.WORK_TYPE_NAME);
            if (workOrderTypeName == null || workOrderTypeName.isEmpty()) {
                throw new DWArgumentException("workOrderTypeName", "workOrderTypeName is null or empty!");
            }

            row.set(DBConstants.CREATE_BY_USERID, create_by_userId);
        }
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object put(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_TYPE);
        String serviceTypeId = "";
        String workOrderTypeName = "";
        for (DWDataRow row : table.getRows()) {
            serviceTypeId = (String) row.get(DBConstants.SERVICE_TYPE_ID);
            if (serviceTypeId == null || serviceTypeId.isEmpty()) {
                throw new DWArgumentException("serviceTypeId", "serviceTypeId is null or empty!");
            }

            workOrderTypeName = (String) row.get(DBConstants.WORK_TYPE_NAME);
            if (workOrderTypeName == null || workOrderTypeName.isEmpty()) {
                throw new DWArgumentException("workOrderTypeName", "workOrderTypeName is null or empty!");
            }
        }
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object delete(List<Object> oids) throws Exception {
        if (oids == null || oids.isEmpty())
            throw new DWArgumentException("id", "id is null or empty!");

        // 連動刪除
        DWDataSetBuilder builder = new DWDataSetBuilder();
        DWDataSet dataset = builder.addTable(DBConstants.WORK_ORDER_TYPE).setDeletedOids(oids).createDataSet();

        // 設定刪除查詢信息
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.addCascadeDeleting(DBConstants.WORK_ORDER_SERVICE_TYPE, DBConstants.WORK_ORDER_TYPE);
        option.addCascadeQuery(DBConstants.WORK_ORDER_TYPE, DBConstants.WORK_ORDER_ENGINEER); //addCascadeDeleting

        Object result = this.dao.execute(dataset, option);

        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object getList(Map<String, Object> params) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append(SQL_TITLE);

        DWQueryCondition condition = new DWQueryCondition();
        List<Object> sqlParams = new ArrayList<Object>();

        if (params.containsKey(DBConstants.SERVICE_TYPE_ID)) {
            condition.addFieldInfo("t.service_type_id", DWQueryValueOperator.Equals, params.get(DBConstants.SERVICE_TYPE_ID));
        }
        if (params.containsKey(DBConstants.WORK_TYPE_ID)) {
            condition.addFieldInfo("t.type_id", DWQueryValueOperator.Equals, params.get(DBConstants.WORK_TYPE_ID));
        }

        if (params.containsKey(DBConstants.ENGINEER)) {
            DWQueryCondition assigneeCondition = new DWQueryCondition().ORJoin()
                    .addFieldInfo("e.assignee_name", DWQueryValueOperator.Like, "%" + params.get(DBConstants.ENGINEER) + "%")
                    .addFieldInfo("e.assignee_id", DWQueryValueOperator.Like, "%" + params.get(DBConstants.ENGINEER) + "%");
            condition.addCondition(assigneeCondition);
        }
        if (params.containsKey(DBConstants.MAIL)) {
            condition.addFieldInfo("e.email", DWQueryValueOperator.Like, "%" + params.get(DBConstants.MAIL) + "%");
        }

        if (condition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
            sql.append(" AND " + conditionResult.getSql());
            sqlParams.addAll(conditionResult.getParametersAsList());
        }
        sql.append(GROUP_AND_ORDER);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        List<Map<String, Object>> result = dao.select(option, sql.toString(), sqlParams.toArray());
        result.forEach(item -> item.put(DBConstants.WORK_TYPE_ID, String.format("%07d", Integer.valueOf(item.get(DBConstants.WORK_TYPE_ID).toString()))));

        return DWServiceResultBuilder.build(result);
    }


    @Override
    public Object getDetailList(List<Object> oids) throws Exception {
        if (oids == null || oids.isEmpty())
            throw new DWArgumentException("id", "id is null or empty!");

        // 連動查詢
        DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
        DWQueryInfo queryInfo = queryInfoBuilder.setOids(oids).setPrimaryKeyName(DBConstants.WORK_TYPE_ID).create();
        queryInfo.setTableName(DBConstants.WORK_ORDER_TYPE);

        // 設定連動查詢信息
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.addCascadeQuery(DBConstants.WORK_ORDER_TYPE, DBConstants.WORK_ORDER_ENGINEER);

        DWDataSet dataset = this.dao.select(queryInfo, option);

        DWDataTable workOrderTypeTable = dataset.getTable(DBConstants.WORK_ORDER_TYPE);
        //type_id
        for (DWDataRow row : workOrderTypeTable.getRows()) {
            Long longTypeId = row.get(DBConstants.WORK_TYPE_ID);
            String type_id = String.format("%07d", longTypeId);
            row.set(DBConstants.WORK_TYPE_ID, type_id);
        }

        DWDataTable workOrderEngineerTable = dataset.getTable(DBConstants.WORK_ORDER_ENGINEER);
        //type_id
        for (DWDataRow row : workOrderEngineerTable.getRows()) {
            Long longTypeId = row.get(DBConstants.WORK_TYPE_ID);
            String type_id = String.format("%07d", longTypeId);
            row.set(DBConstants.WORK_TYPE_ID, type_id);
        }

        return DWServiceResultBuilder.build(dataset);
    }
}
