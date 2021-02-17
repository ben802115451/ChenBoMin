package com.digiwin.workorder.dwworkorder.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataRowState;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.validation.DWVDTable;
import com.digiwin.app.service.DWService;

import java.util.Map;

/**
 * @author Miko
 */
public interface IWorkOrderEngineerService extends DWService {
    /**
     * 新增工程人員
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(@DWVDTable(name = "work_order_engineer", operations = DWDataRowState.CREATE_OPERATION)DWDataSet dataset) throws Exception;

    /**
     * 修改工程人員
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object put(@DWVDTable(name = "work_order_engineer", operations = DWDataRowState.UPDATE_OPERATION)DWDataSet dataset) throws Exception;

    /**
     * 刪除工程人員
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Object delete(String id, String type_id) throws Exception;

      //不用寫這個api，因為在工單類型的服務裡面有一個取單身的api，就可以取得該工單類型底下的工程人員清單
//    /**
//     * 取得工程人員清單
//     *
//     * @param queryInfo
//     * @return
//     * @throws Exception
//     */
//    public Object getList(DWPagableQueryInfo queryInfo) throws Exception;

    /**
     * 指定工程人員為主要負責人
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Object putIsPrincipal(String id, String type_id) throws Exception;

}
