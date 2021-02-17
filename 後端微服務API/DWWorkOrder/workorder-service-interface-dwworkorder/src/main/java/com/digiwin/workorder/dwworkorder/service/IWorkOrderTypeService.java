package com.digiwin.workorder.dwworkorder.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataRowState;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.validation.DWVDTable;
import com.digiwin.app.service.DWService;

import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public interface IWorkOrderTypeService extends DWService {
    /**
     * 新增工單類型
     *
     * @param dataset
     * @return
     * @throws Exception
     */

    public Object post(@DWVDTable(name = "work_order_type", operations = DWDataRowState.CREATE_OPERATION)DWDataSet dataset) throws Exception;

    /**
     * 修改工單類型
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(@DWVDTable(name = "work_order_type", operations = DWDataRowState.UPDATE_OPERATION)DWDataSet dataset) throws Exception;

    /**
     * 刪除工單類型
     * @param oids
     * @return
     * @throws Exception
     */
    public Object delete(@DWVDTable(name = "work_order_type", operations = DWDataRowState.DELETE_OPERATION)List<Object> oids) throws Exception;

    /**
     * 取得工單類型清單
     * @param params
     * @return
     * @throws Exception
     */
    public Object getList(Map<String, Object> params) throws Exception;

    /**
     * 取得工單類型詳細清單
     * @return
     * @throws Exception
     */
    public Object getDetailList(List<Object> oids) throws Exception;



}
