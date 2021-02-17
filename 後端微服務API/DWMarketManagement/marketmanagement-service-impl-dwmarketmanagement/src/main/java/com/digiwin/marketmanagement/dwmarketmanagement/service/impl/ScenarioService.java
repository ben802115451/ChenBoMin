package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.marketmanagement.dwmarketmanagement.service.IScenarioService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Miko
 */
public class ScenarioService implements IScenarioService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    private static final Log log = LogFactory.getLog(ScenarioService.class);


    /**
     * 新增情境圖
     * S-A、S-B、S-C、S-D只能唯一
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object post(DWDataSet dataset) throws Exception {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        String lastModifyUserId = (String) profile.get("userId");
        String lastModifyUserName = (String) profile.get("userName");

        DWDataTable table = dataset.getTable(DBConstants.LAYOUT_ARRANGEMENT);

        List<Object> areaTypeList = new ArrayList<>();
        String areaType = "";
        for (DWDataRow row : table.getRows()) {
            areaType = (String) row.get(DBConstants.AREA_TYPE);
            if (areaType == null || areaType.isEmpty()) {
                throw new DWArgumentException("areaType", "areaType is null or empty!");
            }

            row.set(DBConstants.LAST_MODIFY_USERID, lastModifyUserId);
            row.set(DBConstants.LAST_MODIFY_USERNAME, lastModifyUserName);
            row.set(DBConstants.AREA_TYPE, "S-" + areaType);
            areaTypeList.add("S-" + areaType);
        }

        //先把入參的areaType存放在資料庫的資料做刪除
        StringBuilder sql = new StringBuilder();
        sql = sql.append(DBConstants.DELETE_AREATYPE);
        DWQueryCondition condition = new DWQueryCondition();
        List<Object> sqlParams = new ArrayList<Object>();
        condition.addFieldInfo(DBConstants.AREA_TYPE, DWQueryValueOperator.In, areaTypeList.toArray());

        if (condition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
            sql.append(conditionResult.getSql());
            sqlParams.addAll(conditionResult.getParametersAsList());
        }
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        this.dao.update(option, sql.toString(), sqlParams.toArray());
        //刪除原本的資料就可以依照入參做新增
        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    /**
     * 查詢情境圖
     *
     * @param areaType
     * @return
     * @throws Exception
     */
    @Override
    public Object get(String areaType) throws Exception {
        DWQueryInfo queryInfo = new DWQueryInfo(DBConstants.LAYOUT_ARRANGEMENT);
        queryInfo.addFieldInfo("areaType", DWQueryValueOperator.Equals, "S-" + areaType);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        DWDataSet dataSet = dao.select(queryInfo, option);
        String reslutAreaType = "";
        DWDataTable tableName = dataSet.getTable(DBConstants.LAYOUT_ARRANGEMENT);
        if (tableName.getRows().size() != 0) {
            String tmpAreaType = tableName.getRow(0).get("areaType");
            reslutAreaType = tmpAreaType.substring(tmpAreaType.length() - 1);
        }
        tableName.getRow(0).set("areaType", reslutAreaType);
        return DWServiceResultBuilder.build(dataSet);
    }

    @Override
    public Object put(DWDataSet dataset) throws Exception {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        String lastModifyUserId = (String) profile.get("userId");
        String lastModifyUserName = (String) profile.get("userName");

        DWDataTable table = dataset.getTable(DBConstants.LAYOUT_ARRANGEMENT);
        String areaType = "";
        for (DWDataRow row : table.getRows()) {
            areaType = (String) row.get(DBConstants.AREA_TYPE);
            if (areaType == null || areaType.isEmpty()) {
                throw new DWArgumentException("areaType", "areaType is null or empty!");
            }

            row.set(DBConstants.LAST_MODIFY_USERID, lastModifyUserId);
            row.set(DBConstants.LAST_MODIFY_USERNAME, lastModifyUserName);
            row.set(DBConstants.AREA_TYPE, "S-" + areaType);
        }
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }
}
