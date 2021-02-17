package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.data.exceptions.DWDataTableNotFoundException;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.marketmanagement.dwmarketmanagement.service.ICloudThemeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * @author Miko
 */
public class CloudThemeService implements ICloudThemeService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    private static final Log log = LogFactory.getLog(CloudThemeService.class);

    GoodsTypeService goodsTypeService = new GoodsTypeService();

    @Override
    public Object post(DWDataSet dataset) throws Exception {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        String createUserId = (String) profile.get("userId");
        String createUserName = (String) profile.get("userName");
        try {
            DWDataTable table = dataset.getTable(DBConstants.CLOUD_THEME);
            String themeName = "";
            for (DWDataRow row : table.getRows()) {
                themeName = (String) row.get(DBConstants.THEME_NAME);
                if (themeName == null || themeName.isEmpty()) {
                    throw new DWArgumentException("themeName", "themeName is null or empty!");
                }

                row.set(DBConstants.CREATE_USERID, createUserId);
                row.set(DBConstants.CREATE_USERNAME, createUserName);
            }
        } catch (DWDataTableNotFoundException e) {
            // 找不到表 不做事
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    //編輯把排序寫進來
    @Override
    public Object put(DWDataSet dataset) throws Exception {
        try {
            DWDataTable table = dataset.getTable(DBConstants.CLOUD_THEME);
            for (DWDataRow row : table.getRows()) {
                String themeName = (String) row.get(DBConstants.THEME_NAME);

                if (themeName == null || themeName.isEmpty()) {
                    throw new DWArgumentException("themeName", "themeName is null or empty!");
                }
            }
        } catch (DWDataTableNotFoundException e) {
            // 找不到表 不做事
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    /**
     * 刪除主題
     *
     * @param oids
     * @return
     * @throws Exception
     */
    @Override
    public Object delete(List<Object> oids) throws Exception {
        if (oids == null || oids.isEmpty())
            throw new DWArgumentException("oid", "oid is null or empty!");

        StringBuilder selectOidsSql = new StringBuilder();
        selectOidsSql = selectOidsSql.append(DBConstants.SELECT_ID);
        DWQueryCondition selectOidsCondition = new DWQueryCondition();
        List<Object> selectOidsSqlParams = new ArrayList<Object>();
        selectOidsCondition.addFieldInfo(DBConstants.ID, DWQueryValueOperator.In, oids.toArray());

        if (selectOidsCondition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(selectOidsCondition);
            selectOidsSql.append(conditionResult.getSql());
            selectOidsSqlParams.addAll(conditionResult.getParametersAsList());
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> oidList = this.dao.select(option, selectOidsSql.toString(), selectOidsSqlParams.toArray());

        //如果商品已上架要告知已上架
        if (oidList.size() > 0) {
            if (oidList.get(0).get(DBConstants.STATUS).equals(1)) {
                throw new DWBusinessException("此主題上架中，無法刪除");
            }
        } else {
            //如果傳入的id沒有在資料庫內要拋錯
            throw new DWBusinessException(String.format("找不到指定的主題 oid = %s", oids));
        }

        /*
        StringBuilder deleteOidsSql = new StringBuilder();
        deleteOidsSql = deleteOidsSql.append(DBConstants.DELETE_THEME);
        DWQueryCondition deleteOidsCondition = new DWQueryCondition();
        List<Object> deleteOidsSqlParams = new ArrayList<Object>();
        deleteOidsCondition.addFieldInfo(DBConstants.ID, DWQueryValueOperator.In, oids.toArray());

        if (deleteOidsCondition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(deleteOidsCondition);
            deleteOidsSql.append(conditionResult.getSql());
            deleteOidsSqlParams.addAll(conditionResult.getParametersAsList());
        }


        Object result = this.dao.update(deleteOidsSql.toString(), deleteOidsSqlParams.toArray());

         */

        DWDataSetBuilder builder = new DWDataSetBuilder();
        DWDataSet dataset = builder.addTable(DBConstants.CLOUD_THEME).setDeletedOids(oids).createDataSet();

        option.addCascadeDeleting(DBConstants.CLOUD_THEME, DBConstants.GOODS_DETAIL);

        Object result = this.dao.execute(dataset, option);


        return DWServiceResultBuilder.build("主題刪除成功", result);
    }

    /**
     * 取得5筆上架資訊
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getActiveList() throws Exception {
        DWQueryInfo queryInfo = new DWQueryInfo();
        queryInfo.setTableName(DBConstants.CLOUD_THEME);
        queryInfo.addFieldInfo(DBConstants.STATUS, DWQueryValueOperator.Equals, "1");
        List<DWQueryOrderby> orderByList = queryInfo.getOrderfields();
        DWQueryOrderby orderBy = new DWQueryOrderby();
        orderBy.setName(DBConstants.SEQUENCE);
        orderBy.setOrderby("ASC");
        orderByList.add(orderBy);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        DWDataSet dataset = this.dao.select(queryInfo, option);
        return DWServiceResultBuilder.build(dataset);
    }

    /**
     * 取得所有下架資訊
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getInactiveList() throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.select(option, DBConstants.SELECT_INACTIVE_LIST);
        return DWServiceResultBuilder.build(result);
    }

    /**
     * 主題上下架(最多5筆上架)
     *
     * @param oid
     * @return
     * @throws Exception
     */
    @Override
    public Object putActive(String oid) throws Exception {
        if (oid == null || oid.isEmpty())
            throw new DWArgumentException("oid", "oid is null or empty!");

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        String msg = "";
        //by入參的themeId
        List<Map<String, Object>> oidList = this.dao.select(option, DBConstants.SELECT_ID + "id = ?", oid);
        if (oidList.size() != 0) {
            if ((int) oidList.get(0).get("status") == 1) {
                msg = "主題上架成功";
            } else {
                //查詢cloud_theme這張表狀態為1(上架)的資料
                List<Map<String, Object>> sourceData = this.dao.select(option, DBConstants.SELECT_ACTIVE_LIST);

                //檢查目前上架的筆數(必需<=5)
                int activeCount = sourceData.size();
                if (activeCount >= 5) {
                    throw new DWArgumentException("activeCount", "activeCount cannot over 5!");
                } else {
                    this.dao.update(option, DBConstants.UPDATE_TO_ACTIVE, oid);
                    msg = "主題上架成功";
                }
            }
        }

        return DWServiceResultBuilder.build(msg);
    }

    /**
     * 主題下架
     *
     * @param oid
     * @return
     * @throws Exception
     */
    @Override
    public Object putInactive(String oid) throws Exception {
        if (oid == null || oid.isEmpty())
            throw new DWArgumentException("oid", "oid is null or empty!");

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.update(option, DBConstants.UPDATE_TO_INACTIVE, oid);
        return DWServiceResultBuilder.build("主題下架成功", result);
    }


    /**
     * 新增商品
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    @Override
    public Object postGoods(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        try {
            DWDataTable table = dataset.getTable(DBConstants.GOODS_DETAIL);
            String themeId = "";
            String goodsCode = "";
            for (DWDataRow row : table.getRows()) {
                themeId = (String) row.get(DBConstants.THEME_ID);
                goodsCode = (String) row.get(DBConstants.GOODS_CODE);
                if (themeId == null || themeId.isEmpty()) {
                    throw new DWArgumentException("themeId", "themeId is null or empty!");
                }
                if (goodsCode == null || goodsCode.isEmpty()) {
                    throw new DWArgumentException("goodsCode", "goodsCode is null or empty!");
                }

                List<Map<String, Object>> themeIdList = this.dao.select(option, DBConstants.SELECT_THEME_ID, themeId);
                //如果商品已上架要告知已上架
                if (themeIdList.size() == 0) {
                    //如果傳入的id沒有在資料庫內要拋錯
                    throw new DWBusinessException(String.format("查無此主題 themeId = %s", themeId));
                }
            }
        } catch (DWDataTableNotFoundException e) {
            // 找不到表 不做事
        }

        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);
    }

    /**
     * 取得商品列表
     *
     * @param oid
     * @return
     * @throws Exception
     */
    @Override
    public Object getGoodsList(String oid) throws Exception {
        DWQueryInfo queryInfo = new DWQueryInfo(DBConstants.GOODS_DETAIL);
        queryInfo.addFieldInfo("themeId", DWQueryValueOperator.Equals, oid);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        DWDataSet dataSet = dao.select(queryInfo, option);
        DWDataTable tableName = dataSet.getTable(DBConstants.GOODS_DETAIL);
        List<String> goodsList = new ArrayList<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        String goodsCode = "";
        if (tableName.getRows().size() != 0) {
            for (DWDataRow row : tableName.getRows()) {
                goodsCode = row.get("goodsCode");
                if (goodsCode != null || StringUtils.isNotEmpty(goodsCode)) {
                    goodsList.add(goodsCode);
                }

                resultList = goodsTypeService.get(goodsList);

                for (Map<String, Object> map : resultList) {
                    row.set(DBConstants.GOODS_NAME, map.get("displayName"));
                    row.set(DBConstants.CATAGORY_ID, map.get("categoryId"));
                }

            }
        }

        return DWServiceResultBuilder.build(dataSet);
    }
}
