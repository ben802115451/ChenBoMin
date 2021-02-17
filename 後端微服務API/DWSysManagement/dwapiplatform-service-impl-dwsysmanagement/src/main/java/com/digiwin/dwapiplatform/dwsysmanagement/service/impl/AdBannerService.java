package com.digiwin.dwapiplatform.dwsysmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.dwapiplatform.dwsysmanagement.service.IAdBannerService;

@SuppressWarnings("deprecation")
public class AdBannerService implements IAdBannerService {

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    // 新增輪播廣告資訊
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public Object post(DWDataSet dataset) throws Exception {

        // 單頭表邏輯
        //DWDataTable table = dataset.getTable(DBConstants.ADBANNER_MASTER);

        //DWDataSetOperationOption option = new DWDataSetOperationOption();

        DWSQLExecutionResult result = dao.execute(dataset);

        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

    }

    // 修改輪播廣告資訊
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public Object put(DWDataSet dataset) throws Exception {
        DWSQLExecutionResult result = dao.execute(dataset);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

    }

    // 刪除輪播廣告
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public Object delete(DWDataSet dataset) throws Exception {

        // 連動刪除
        //DWDataSetOperationOption option = new DWDataSetOperationOption();
        //option.addCascadeDeleting(DBConstants.SYSMAN_ADBANNER_MASTER, DBConstants.SYSMAN_ADBANNER_HISTORY);
        //Object result = this.dao.execute(dataset, option);
        Object result = this.dao.execute(dataset);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);

    }

    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
        queryInfo.setTableName(DBConstants.SYSMAN_ADBANNER_MASTER);
        Object result = this.dao.selectWithPage(queryInfo);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
    }

    /*
     * @Override public Object getChangeHistory(Map<String, Object> param) throws
     * Exception { DWQueryInfo queryInfo = new DWQueryInfo();
     * queryInfo.setTableName(DBConstants.SYSMAN_ADBANNER_HISTORY);
     *
     * DWQueryCondition condition = new DWQueryCondition(); for(String key:
     * param.keySet()) { condition.addEqualInfo(key, param.get(key)); }
     * queryInfo.setCondition(condition );
     *
     * DWDataSet dbData = this.dao.select(queryInfo);
     *
     *
     * Object result = dbData; return DWServiceResultBuilder.build(true,
     * DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001",
     * "msg001"), result); }
     */

    @Override
    public Object getBlocks(DWQueryInfo queryInfo) throws Exception {
        queryInfo.setTableName(DBConstants.SYSMAN_ADBANNER_BLOCK);
        Object result = this.dao.select(queryInfo);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
    }

    @Override
    public Object getRotateList(DWQueryInfo queryInfo) throws Exception {
        queryInfo.setTableName(DBConstants.SYSMAN_ADBANNER_MASTER);
        Object result = this.dao.select(queryInfo);
        return DWServiceResultBuilder.build(true, DWModuleMessageResourceBundleUtils.getCurrentModuleResourceBundle("msg001", "msg001"), result);
    }
}
