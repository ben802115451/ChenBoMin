package com.digiwin.developer.dwdeveloper.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.developer.dwdeveloper.service.IFileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Miko
 */
public class FileManagementService implements IFileManagementService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    @Override
    public Object post(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        //dataset中的其中一個值應該傳goodsCode就好，用此goodsCode去檢查是否有在該租戶下所購買的開發類應用
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        String createUserId = (String) profile.get("userId");
        String createUserName = (String) profile.get("userName");
        DWDataTable table = dataset.getTable(DBConstants.FILE_MANAGEMENT);

        String displayFileName = "";
        String fileName = "";
        String goodsCode = "";
        String goodsName = "";
        String description = "";

        for (DWDataRow row : table.getRows()) {
            displayFileName = (String) row.get(DBConstants.DISPLAY_FILENAME);
            if (displayFileName == null || displayFileName.isEmpty()) {
                throw new DWArgumentException("displayName", "displayName is null or empty!");
            }

            fileName = (String) row.get(DBConstants.FILENAME);
            if (fileName == null || fileName.isEmpty()) {
                throw new DWArgumentException("fileName", "fileName is null or empty!");
            }

            goodsCode = (String) row.get(DBConstants.GOODS_CODE);
            if (goodsCode == null || goodsCode.isEmpty()) {
                throw new DWArgumentException("goodsCode", "goodsCode is null or empty!");
            }

            goodsName = (String) row.get(DBConstants.GOODS_NAME);
            if (goodsName == null || goodsName.isEmpty()) {
                throw new DWArgumentException("goodsName", "goodsName is null or empty!");
            }

            description = (String) row.get(DBConstants.DESCRIPTION);
            if (description == null || description.isEmpty()) {
                throw new DWArgumentException("description", "description is null or empty!");
            }

            row.set(DBConstants.CREATE_USERID, createUserId);
            row.set(DBConstants.CREATE_USERNAME, createUserName);
        }


        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object put(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        DWDataTable table = dataset.getTable(DBConstants.FILE_MANAGEMENT);
        for (DWDataRow row : table.getRows()) {
            String displayFileName = (String) row.get(DBConstants.DISPLAY_FILENAME);

            if (displayFileName == null || displayFileName.isEmpty()) {
                throw new DWArgumentException("displayFileName", "displayFileName is null or empty!");
            }

            String fileName = (String) row.get(DBConstants.FILENAME);

            if (fileName == null || fileName.isEmpty()) {
                throw new DWArgumentException("fileName", "fileName is null or empty!");
            }

            String goodsCode = (String) row.get(DBConstants.GOODS_CODE);

            if (goodsCode == null || goodsCode.isEmpty()) {
                throw new DWArgumentException("goodsCode", "goodsCode is null or empty!");
            }

            String description = (String) row.get(DBConstants.DESCRIPTION);

            if (description == null || description.isEmpty()) {
                throw new DWArgumentException("description", "description is null or empty!");
            }
        }
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object delete(String id) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DBConstants.ID, id);
        map.put("$state", "D");
        DWDataSet dataSet = new DWDataSet();
        dataSet.newTable(DBConstants.FILE_MANAGEMENT).newRow(map);
        DWSQLExecutionResult result = this.dao.execute(dataSet, option);

        return DWServiceResultBuilder.build("文檔刪除成功", result);

    }

    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        queryInfo.setTableName(DBConstants.FILE_MANAGEMENT);
        Object result = this.dao.selectWithPage(queryInfo, option);

        return DWServiceResultBuilder.build(result);

    }

    @Override
    public Object getDetailList(String id) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        DWQueryInfo queryInfo = new DWQueryInfo();
        queryInfo.setTableName(DBConstants.FILE_MANAGEMENT);
        queryInfo.addFieldInfo(DBConstants.ID, DWQueryValueOperator.Equals, id);
        DWDataSet dataset = this.dao.select(queryInfo , option);
        return DWServiceResultBuilder.build(dataset);
    }

    @Override
    public Object putActive(String id) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DBConstants.ID, id);
        map.put(DBConstants.STATUS, "1");
        map.put("$state", "U");
        DWDataSet dataSet = new DWDataSet();
        dataSet.newTable(DBConstants.FILE_MANAGEMENT).newRow(map);
        DWSQLExecutionResult result = this.dao.execute(dataSet, option);

        return DWServiceResultBuilder.build("文檔上架成功", result);
    }

    @Override
    public Object putInactive(String id) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DBConstants.ID, id);
        map.put(DBConstants.STATUS, "0");
        map.put("$state", "U");
        DWDataSet dataSet = new DWDataSet();
        dataSet.newTable(DBConstants.FILE_MANAGEMENT).newRow(map);
        DWSQLExecutionResult result = this.dao.execute(dataSet, option);

        return DWServiceResultBuilder.build("文檔下架成功", result);
    }
}
