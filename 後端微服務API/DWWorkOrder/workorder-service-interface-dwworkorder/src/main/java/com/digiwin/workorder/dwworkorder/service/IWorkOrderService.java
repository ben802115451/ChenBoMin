package com.digiwin.workorder.dwworkorder.service;

import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataRowState;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.validation.DWVDTable;
import com.digiwin.app.service.DWService;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Bomin
 */
@Validated
public interface IWorkOrderService extends DWService {

    /**
     * 新增工單
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object post(@DWVDTable(name = "work_order", operations = DWDataRowState.CREATE_OPERATION) @DWVDTable(name = "work_order_attachment", operations = DWDataRowState.CREATE_OPERATION) DWDataSet dataset) throws Exception;

    /**
     * 取得工單詳情
     *
     * @param oids
     * @return
     * @throws Exception
     */
    public DWDataSet get(List<Integer> oids) throws Exception;

    /**
     * 取得工單列表
     *
     * @param pagableQueryInfo
     * @return
     * @throws Exception
     */
    public Object getList(DWPagableQueryInfo pagableQueryInfo) throws Exception;

    /**
     * 刪除工單
     *
     * @return
     * @throws Exception
     */
    public Object delete(Integer oid) throws Exception;

    /**
     * 將工單關閉
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putClose(Integer oid) throws Exception;

    /**
     * 將工單撤單
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putCancel(Integer oid) throws Exception;

    /**
     * 受理工單按鈕
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object putAccept(Integer oid, String message) throws Exception;

    /**
     * 已解決按鈕
     *
     * @return
     * @throws Exception
     */
    public Object putSolved(Integer oid, String message) throws Exception;

    /**
     * 轉派工單
     *
     * @return
     * @throws Exception
     */
    public Object putTransfer(@DWVDTable(name = "work_order", operations = DWDataRowState.UPDATE_OPERATION)DWDataSet dataset) throws Exception;

    /**
     * 新增備註
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object postRemark(@DWVDTable(name = "work_order_remark", operations = DWDataRowState.CREATE_OPERATION) DWDataSet dataset) throws Exception;

    /**
     * 取得備註
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public Object getRemark(Integer oid) throws Exception;

    /**
     * 應用人員回復工單服務
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object postMessageByAssignee(DWDataSet dataset) throws Exception;

    /**
     * 用戶回復工單服務
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    public Object postMessageByUser(DWDataSet dataset) throws Exception;
}
