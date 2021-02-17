package com.digiwin.util;

import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWPaginationQueryResult;
import com.digiwin.app.dao.DWQueryValueOperator;
import com.digiwin.app.module.spring.SpringContextUtils;

/**
 * 單據編號搜尋器<br>
 *
 * @author Bomin
 */
public class DocumentIdFinder {

    /**
     * 取得單據最大流水號<br>
     *
     * @param group   prefix+年月日8位組合
     * @param setting 取號參數設定檔
     * @return
     */
    public String getMaxSerialNo(String group, DocumentIdSetting setting) throws Exception {

        DWDao targetDao;
        String documentId = null;

        DWPagableQueryInfo queryInfo = new DWPagableQueryInfo();

        queryInfo.setSelectFields(setting.getColumnName());
        queryInfo.setTableName(setting.getTableName());
        queryInfo.addFieldInfo(setting.getColumnName(), DWQueryValueOperator.Like, group + "%");
        queryInfo.addOrderBy(setting.getColumnName(), false);
        queryInfo.setPageSize(1);
        queryInfo.setPageNumber(1);

        //默認使用外層Dao
        targetDao = setting.getDao() == null ? getDefaultDao() : setting.getDao();

        if (targetDao == null)
            throw new DWException("Dao not found, Please check the dao bean or setting.setDao");

        DWPaginationQueryResult data = targetDao.selectWithPage(queryInfo);

        if (data.getDataSet().getTable(setting.getTableName()).getRows().size() != 0)
            documentId = data.getDataSet().getTable(setting.getTableName()).getRow(0).get(setting.getColumnName());

        return documentId;
    }

    private DWDao getDefaultDao() {

        DWDao defaultDao = null;

        try {
            defaultDao = SpringContextUtils.getBean("dw-dao");
        } catch (Exception e) {
        }

        return defaultDao;
    }
}
