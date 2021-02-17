package com.digiwin.developer.dwdeveloper.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataRowState;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.validation.DWVDTable;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

/**
 * @author Bomin
 */
@Validated
public interface IGoodsThemeService extends DWService {

    /**
     * 新增主題
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(@DWVDTable(name = "goods_theme", operations = DWDataRowState.CREATE_OPERATION) DWDataSet dataset) throws Exception;

    /**
     * 修改主題
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(@DWVDTable(name = "goods_theme", operations = DWDataRowState.UPDATE_OPERATION) DWDataSet dataset) throws Exception;

    /**
     * 取得主題列表
     *
     * @param queryInfo
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    /**
     * 刪除主題
     *
     * @return
     * @throws Exception
     */
    public Object delete(List<Object> oids) throws Exception;

    /**
     * 將主題顯示
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putDisplay(Integer oid) throws Exception;

    /**
     * 將主題關閉
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putClose(Integer oid) throws Exception;

    /**
     * 查詢顯示主題列表詳情
     *
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public Object getDisplay() throws Exception;

    /**
     * 設置商品
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object postGoods(Integer oid, @DWVDTable(name = "goods_theme_detail", operations = DWDataRowState.CREATE_OPERATION) DWDataSet dataset) throws Exception;

    /**
     * 取得商品列表
     *
     * @param oids
     * @return
     * @throws Exception
     */
    public Object get(List<Integer> oids) throws Exception;

    /**
     * 取得已使用商品列表
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getUsedGoodsList() throws Exception;
}
