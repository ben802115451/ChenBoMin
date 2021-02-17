package com.digiwin.developer.dwdeveloper.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;

import com.digiwin.developer.dwdeveloper.service.IGoodsThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class GoodsThemeService implements IGoodsThemeService {

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    // 資料表
    static final String GOODS_THEME = "goods_theme";
    static final String GOODS_THEME_DETAIL = "goods_theme_detail";

    // 資料表欄位
    static final String THEMEID = "themeid";
    static final String STATUS = "status";
    static final String SEQ = "seq";

    //SQL語句
    static final String SELECT_GOODS_THEME_DETAIL_USED = "SELECT goods_theme.themeName, goods_theme_detail.goodsCode FROM goods_theme_detail INNER JOIN goods_theme ON goods_theme_detail.themeid = goods_theme.themeid;";
    static final String UPDATE_GOODS_THEME_STATUS_DISPLAY = "UPDATE goods_theme SET STATUS = 1 WHERE themeid = ? ";
    static final String UPDATE_GOODS_THEME_STATUS_CLOSE = "UPDATE goods_theme SET STATUS = 0 WHERE themeid = ? ";
    static final String DELETE_GOODS_THEME_DETAIL = "DELETE FROM goods_theme_detail WHERE themeid = ?";

    //新增主題
    @Override
    public Object post(DWDataSet dataset) throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWSQLExecutionResult result = dao.execute(dataset, option);

        return DWServiceResultBuilder.build("post success", result);
    }

    //編輯主題
    @Override
    public Object put(DWDataSet dataset) throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWSQLExecutionResult result = dao.execute(dataset, option);

        return DWServiceResultBuilder.build("put success", result);
    }

    //取得主題列表
    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {

        queryInfo.setTableName(GOODS_THEME);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        Object result = dao.selectWithPage(queryInfo, option);
        return DWServiceResultBuilder.build(result);
    }

    //刪除主題
    @Override
    public Object delete(List<Object> oids) throws Exception {

        Object result = null;

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        if (oids.size() > 0) {
            for (Object oid : oids) {
                DWQueryInfo queryInfo = new DWQueryInfo(GOODS_THEME).addEqualInfo(STATUS, 1).addEqualInfo(THEMEID, oid);
                DWDataSet dataSetResult = dao.select(queryInfo, option);
                if (dataSetResult.getTable(GOODS_THEME).getRows().size() > 0) {
                    throw new DWBusinessException(String.format("themeid :「%s」is displaying, Can't delete", new Double((double) oid).intValue()));
                }
            }

            DWDataSetBuilder builder = new DWDataSetBuilder();
            DWDataSet dataset = builder.addTable(GOODS_THEME).setDeletedOids(oids).createDataSet();

            // 設定連動刪除信息
            option.addCascadeDeleting(GOODS_THEME, GOODS_THEME_DETAIL);

            result = dao.execute(dataset, option);
        }
        return DWServiceResultBuilder.build(result);

    }

    //取得顯示主題列表詳情
    @Override
    public Object getDisplay() throws Exception {

        DWQueryInfo queryInfo = new DWQueryInfo(GOODS_THEME).addEqualInfo(STATUS, 1).addOrderBy(SEQ);

        // 設定連動查詢信息
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.addCascadeQuery(GOODS_THEME, GOODS_THEME_DETAIL);

        Object result = dao.select(queryInfo, option);

        return DWServiceResultBuilder.build(result);
    }

    // 將主題顯示
    @Override
    public Object putDisplay(Integer oid) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        Object result = dao.update(option, UPDATE_GOODS_THEME_STATUS_DISPLAY, oid);

        return DWServiceResultBuilder.build(result);
    }

    //將主題關閉
    @Override
    public Object putClose(Integer oid) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        Object result = dao.update(option, UPDATE_GOODS_THEME_STATUS_CLOSE, oid);

        return DWServiceResultBuilder.build(result);
    }

    /**
     * 設置商品
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object postGoods(Integer oid, DWDataSet dataset) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }

        dataset.getTable(GOODS_THEME_DETAIL).getRows().forEach(item -> item.set(THEMEID, oid));

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        dao.update(option, DELETE_GOODS_THEME_DETAIL, oid); //刪除原始資料

        Object result = dao.execute(dataset, option);


        return DWServiceResultBuilder.build("Goods post success", result);
    }

    /**
     * 取得主題商品列表
     *
     * @param oids
     * @return
     * @throws Exception
     */
    @Override
    public Object get(List<Integer> oids) throws Exception {

        DWQueryInfo queryInfo = new DWQueryInfo(GOODS_THEME).addOrderBy(SEQ);
        if (oids.size() > 0)
            queryInfo.addFieldInfo(THEMEID, DWQueryValueOperator.In, oids.toArray());
        // 設定連動查詢信息
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.addCascadeQuery(GOODS_THEME, GOODS_THEME_DETAIL);

        Object result = dao.select(queryInfo, option);

        return result;
    }

    /**
     * 取得已使用商品列表
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getUsedGoodsList() throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> daoResult = dao.select(option, SELECT_GOODS_THEME_DETAIL_USED);

        return daoResult;
    }
}
