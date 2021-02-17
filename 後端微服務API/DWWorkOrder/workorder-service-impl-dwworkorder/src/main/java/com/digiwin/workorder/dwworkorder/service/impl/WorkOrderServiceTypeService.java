package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.workorder.dwworkorder.service.IWorkOrderServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public class WorkOrderServiceTypeService implements IWorkOrderServiceTypeService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    @Override
    public Object post(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_SERVICE_TYPE);
        String serviceName = "";
        for (DWDataRow row : table.getRows()) {
            serviceName = row.get(DBConstants.SERVICE_NAME);
            if (serviceName == null || serviceName.isEmpty()) {
                throw new DWArgumentException("serviceName", "serviceName is null or empty!");
            }
        }
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object put(DWDataSet dataset) throws Exception {
        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_SERVICE_TYPE);
        String serviceName = "";
        String serviceId = "";

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        for (DWDataRow row : table.getRows()) {
            serviceName = (String) row.get(DBConstants.SERVICE_NAME);
            if (serviceName == null || serviceName.isEmpty()) {
                throw new DWArgumentException("serviceName", "serviceName is null or empty!");
            }
            serviceId = (String) row.get(DBConstants.SERVICE_ID);
            if (serviceId == null || serviceId.isEmpty()) {
                throw new DWArgumentException("serviceId", "serviceId is null or empty!");
            }
        }
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object delete(String id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DBConstants.SERVICE_ID, id);
        map.put("$state", "D");
        DWDataSet dataSet = new DWDataSet();
        dataSet.newTable(DBConstants.WORK_ORDER_SERVICE_TYPE).newRow(map);
        DWSQLExecutionResult result = this.dao.execute(dataSet, option);

        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        queryInfo.setTableName(DBConstants.WORK_ORDER_SERVICE_TYPE);
        queryInfo.addOrderBy(DBConstants.SEQUENCE); //by sequence ASC 由小到大排序)

        DWDataSet dataset = this.dao.select(queryInfo, option);
        DWDataTable table = dataset.getTable(DBConstants.WORK_ORDER_SERVICE_TYPE);

        for (DWDataRow row : table.getRows()) {
            Long longServiceId = row.get(DBConstants.SERVICE_ID);
            String service_id = String.format("%05d", longServiceId);
            row.set(DBConstants.SERVICE_ID, service_id);
        }

        return DWServiceResultBuilder.build(dataset);
    }

    @Override
    public Object getDetailList(String field, List<Object> oids) throws Exception {

        String serviceTypeOrderBy = field;
        String typeOrderBy = field;

        if (field.equalsIgnoreCase("id")) {
            serviceTypeOrderBy = DBConstants.SERVICE_ID;
            typeOrderBy = DBConstants.SERVICE_TYPE_ID;
        }

        DWQueryInfo serviceTypeQueryInfo = new DWQueryInfo(DBConstants.WORK_ORDER_SERVICE_TYPE).addOrderBy(serviceTypeOrderBy);
        DWQueryInfo typeQueryInfo = new DWQueryInfo(DBConstants.WORK_ORDER_TYPE).addOrderBy(typeOrderBy);

        if (oids.size() > 0) {
            serviceTypeQueryInfo.addFieldInfo(DBConstants.SERVICE_ID, DWQueryValueOperator.In, oids.toArray());
        }
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.addCascadeQuery(DBConstants.WORK_ORDER_SERVICE_TYPE, DBConstants.WORK_ORDER_TYPE).setQueryInfo(typeQueryInfo);

        DWDataSet result = dao.select(serviceTypeQueryInfo, option);

        for (DWDataRow primaryRow : result.getTables().getPrimaryTable().getRows()) {
            primaryRow.set(DBConstants.SERVICE_ID, String.format("%05d", Integer.valueOf(primaryRow.get(DBConstants.SERVICE_ID).toString())));
            for (DWDataRow row : (DWReferenceDataRowCollection) primaryRow.get(DBConstants.WORK_ORDER_TYPE)) {
                row.set(DBConstants.WORK_TYPE_ID, String.format("%07d", Integer.valueOf(row.get(DBConstants.WORK_TYPE_ID).toString())));
            }
        }
        result.getSourceMap().remove(DBConstants.WORK_ORDER_TYPE);

        return DWServiceResultBuilder.build(result);
    }
}
