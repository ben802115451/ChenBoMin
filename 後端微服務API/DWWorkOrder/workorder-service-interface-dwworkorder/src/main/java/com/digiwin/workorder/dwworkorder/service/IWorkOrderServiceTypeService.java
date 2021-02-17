package com.digiwin.workorder.dwworkorder.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataRowState;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.validation.DWVDTable;
import com.digiwin.app.service.DWService;

import java.util.List;

/**
 * @author Miko
 */
public interface IWorkOrderServiceTypeService extends DWService {
    /**
     * 新增工單服務別
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(@DWVDTable(name = "work_order_service_type", operations = DWDataRowState.CREATE_OPERATION)DWDataSet dataset) throws Exception;

    /**
     * 修改工單服務別
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(DWDataSet dataset) throws Exception;

    /**
     * 刪除工單服務別
     * @param id
     * @return
     * @throws Exception
     */
    public Object delete(String id) throws Exception;

    /**
     * 取得工單服務別清單
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    /**
     * 取得工單服務別(單身)
     * @return
     * @throws Exception
     */
    public Object getDetailList(String field, List<Object> oids) throws Exception;
}
