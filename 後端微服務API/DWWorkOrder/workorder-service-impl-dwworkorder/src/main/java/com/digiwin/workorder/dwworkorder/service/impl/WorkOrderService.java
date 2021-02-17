package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.workorder.dwworkorder.service.IWorkOrderService;
import com.digiwin.http.client.DWHttpClient;
import com.digiwin.http.client.entity.DWJsonEntity;
import com.digiwin.http.client.utils.DWRequestHeaderUtils;
import com.digiwin.http.client.utils.DWURIBuilder;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class WorkOrderService implements IWorkOrderService {

    @Autowired
    DWHttpClient httpClient;

    @Autowired
    private WorkOrderTypeService workOrderTypeService;

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    // 資料表
    static final String WORK_ORDER = "work_order";
    static final String WORK_ORDER_ATTACHMENT = "work_order_attachment";
    static final String WORK_ORDER_ASSIGNEE_MESSAGE = "work_order_assignee_message";
    static final String WORK_ORDER_REMARK = "work_order_remark";
    static final String WORK_ORDER_USER_MESSAGE = "work_order_user_message";
    static final String WORK_ORDER_USER_MESSAGE_ATTACHMENT = "work_order_user_message_attachment";
    static final String WORK_ORDER_ENGINEER = "work_order_engineer";

    // 資料表欄位
    static final String ORDERID = "orderid";
    static final String TENANTID = "tenantId";
    static final String TENANTNAME = "tenantName";
    static final String USERNAME = "userName";
    static final String USERID = "userId";
    static final String STATUS = "status";
    static final String CREATE_DATE = "create_date";
    static final String SERVICETYPE = "serviceType";
    static final String SERVICETYPEID = "serviceTypeId";
    static final String ORDERTYPEID = "orderTypeId";
    static final String ORDERTYPE = "orderType";
    static final String ORDERTHEME = "orderTheme";
    static final String DEFAULTASSIGNEE = "defaultAssignee";
    static final String DEFAULTASSIGNEEID = "defaultAssigneeId";
    static final String DEFAULTASSIGNEEEMAIL = "defaultAssigneeEmail";
    static final String CURRENTASSIGNEE = "currentAssignee";
    static final String CURRENTASSIGNEEID = "currentAssigneeId";
    static final String CURRENTASSIGNEEEMAIL = "currentAssigneeEmail";
    static final String CONTENT = "content";
    static final String ISPRINCIPAL = "isPrincipal";
    static final String DELETED = "deleted";
    static final String MARK = "mark";
    static final String PREFIX = "prefix";
    static final String EMAIL = "email";
    static final String ASSIGNEE_NAME = "assignee_name";
    static final String ASSIGNEE_ID = "assignee_id";
    static final String CREATE_BY_ID = "create_by_id";
    static final String CREATE_BY_USER_ID = "create_by_user_id";
    static final String MODIFY_BY = "modify_by";
    static final String MODIFY_BY_ID = "modify_by_id";
    static final String MODIFY_DATE = "modify_date";
    static final String CONTACTUSER = "contactUser";
    static final String STARTTIME = "start_time";
    static final String ENDTIME = "end_time";
    static final String PRODUCTADMIN = "{productAdmin}";
    static final String CODE = "code";
    static final String PRODUCTCODE = "dev";

    //發信字段
    static final String WORK_ORDER_SUBMIT_ASSIGNEE = "WORK_ORDER_SUBMIT_ASSIGNEE";
    static final String WORK_ORDER_SUBMIT_USER = "WORK_ORDER_SUBMIT_USER";
    static final String WORK_ORDER_REPLY = "WORK_ORDER_REPLY";

    static final String EVENTID = "eventId";
    static final String CONTACTS = "contacts";
    static final String DATA = "data";
    static final String MESSAGE = "message";

    //Mail模板使用的連結，前往工單中心(om、dev)
    static final String DEVOPSURL = "devopsUrl";
    static final String DEVDEVOPSURL = "devdevopsUrl";

    String omdevopsUrl = DWModuleConfigUtils.getCurrentModuleProperty("omdevopsUrl")
            + DWModuleConfigUtils.getCurrentModuleProperty("omdevopsApi");

    String consoledevopsUrl = DWModuleConfigUtils.getCurrentModuleProperty("consoledevopsUrl")
            + DWModuleConfigUtils.getCurrentModuleProperty("consoledevopsApi");

    //避開多租戶自訂SQL
    static final String PUT_ACCEPT = "UPDATE work_order SET STATUS = '1' ${mgmtFieldUpdateColumns} WHERE orderid = ?";
    static final String PUT_SOLVED = "UPDATE work_order SET STATUS = '2' ${mgmtFieldUpdateColumns} WHERE orderid = ?";
    static final String PUT_TRANSFER = "UPDATE work_order SET serviceTypeId = ?,  serviceType = ?, orderTypeId = ?, " +
            "orderType = ?,  currentAssignee = ?, currentAssigneeId = ?, currentAssigneeEmail = ? ${mgmtFieldUpdateColumns} WHERE orderid = ?";

    //新增工單
    @Override
    public Object post(DWDataSet dataset) throws Exception {

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.getInsertOption().getAutoIncrementOption().addSource(WORK_ORDER, WORK_ORDER_ATTACHMENT, ORDERID);
        StringBuilder email = null;
        String currentAssignee = null;

        for (DWDataRow row : dataset.getTable(WORK_ORDER).getRows()) {

            email = new StringBuilder();
            currentAssignee = new String();

            List<Object> orderTypeIdList = new ArrayList();
            orderTypeIdList.add(row.get(ORDERTYPEID));
            DWServiceResult serviceTypeResult = (DWServiceResult) workOrderTypeService.getDetailList(orderTypeIdList);
            DWDataSet serviceTypeDataSet = (DWDataSet) serviceTypeResult.getData();
            LocalDateTime today = LocalDateTime.now();

            for (DWDataRow serviceTypeRow : serviceTypeDataSet.getTable(WORK_ORDER_ENGINEER).getRows()) {

                if (serviceTypeRow.getData().size() > 0) {
                    Timestamp start_time = serviceTypeRow.get(STARTTIME);
                    Timestamp end_time = serviceTypeRow.get(ENDTIME);
                    if (serviceTypeRow.get(EMAIL) != null && today.isBefore(end_time.toLocalDateTime()) && today.isAfter(start_time.toLocalDateTime())) {
                        email.append(serviceTypeRow.get(EMAIL).toString() + ";");
                    }
                    if ((Integer) serviceTypeRow.get(ISPRINCIPAL) == 1) {

                        row.set(DEFAULTASSIGNEE, serviceTypeRow.get(ASSIGNEE_NAME));
                        row.set(DEFAULTASSIGNEEID, serviceTypeRow.get(ASSIGNEE_ID));
                        row.set(DEFAULTASSIGNEEEMAIL, serviceTypeRow.get(EMAIL));

                        currentAssignee = serviceTypeRow.get(ASSIGNEE_NAME);
                        row.set(CURRENTASSIGNEE, serviceTypeRow.get(ASSIGNEE_NAME));
                        row.set(CURRENTASSIGNEEID, serviceTypeRow.get(ASSIGNEE_ID));
                        row.set(CURRENTASSIGNEEEMAIL, serviceTypeRow.get(EMAIL));
                    }
                }
            }

            row.set(TENANTID, profile.get(TENANTID));
            row.set(TENANTNAME, profile.get(TENANTNAME));
            row.set(CREATE_BY_USER_ID, profile.get(USERID));
        }

        DWSQLExecutionResult result = dao.execute(dataset, option);
        List<Object> generatedKeys = result.getGeneratedKeys(WORK_ORDER);

        for (DWDataRow row : dataset.getTable(WORK_ORDER).getRows()) {

            String orderId = String.format("%07d", generatedKeys.get(0));

            Map<String, Object> requestEntity = new HashMap<>();
            Map<String, Object> messageMap = new HashMap<>();
            Map<String, Object> dataMap = new HashMap<>();

            requestEntity.put(EVENTID, WORK_ORDER_SUBMIT_ASSIGNEE);
            if (email == null || email.length() == 0) {
                requestEntity.put(CONTACTS, PRODUCTADMIN);//產品負責人
                dataMap.put(CODE, PRODUCTCODE);
            } else {
                requestEntity.put(CONTACTS, email);//所有工程人員
            }
            dataMap.put(PREFIX, "*工单提交");
            dataMap.put(ORDERID, orderId);
            dataMap.put(USERNAME, profile.get(USERNAME));
            dataMap.put(USERID, profile.get(USERID));
            dataMap.put(TENANTID, profile.get(TENANTID));
            dataMap.put(TENANTNAME, profile.get(TENANTNAME));
            dataMap.put(STATUS, "待受理");
            dataMap.put(CURRENTASSIGNEE, currentAssignee);
            dataMap.put(SERVICETYPE, row.get(SERVICETYPE));
            dataMap.put(ORDERTYPE, row.get(ORDERTYPE));
            dataMap.put(ORDERTHEME, row.get(ORDERTHEME));
            dataMap.put(CREATE_DATE, row.get(CREATE_DATE));
            dataMap.put(CONTACTUSER, row.get(CONTACTUSER));
            dataMap.put(DEVOPSURL, omdevopsUrl + "?" + ORDERID + "=" + orderId);
            dataMap.put(DEVDEVOPSURL, DWModuleConfigUtils.getCurrentModuleProperty("devdevopsUrl"));

            messageMap.put(DATA, dataMap);
            requestEntity.put(MESSAGE, messageMap);

            postMail(requestEntity); //發信給所有工程人員
        }
        return DWServiceResultBuilder.build("post success", result);
    }

    /**
     * 取得工單詳情
     *
     * @param oids
     * @return
     * @throws Exception
     */
    @Override
    public DWDataSet get(List<Integer> oids) throws Exception {

        DWDataSet result = null;
        if (oids.size() > 0) {

            DWDataSetOperationOption option = new DWDataSetOperationOption();
            Map<String, Object> profile = DWServiceContext.getContext().getProfile();
            if (profile.get(TENANTID).equals("99990000")) {
                option.setTenantEnabled(false);
            }
            DWQueryInfo queryInfo = new DWQueryInfo(WORK_ORDER).addOrderBy(CREATE_DATE);
            queryInfo.addFieldInfo(ORDERID, DWQueryValueOperator.In, oids.toArray());
            result = dao.select(queryInfo, option);

            for (DWDataRow primaryRow : result.getTables().getPrimaryTable().getRows()) {

                Integer orderId = Integer.valueOf(primaryRow.get(ORDERID).toString());
                DWDataSetOperationOption refOption = new DWDataSetOperationOption();
                refOption.setTenantEnabled(false);
                DWQueryInfo ass_queryInfo = new DWQueryInfo(WORK_ORDER_ASSIGNEE_MESSAGE).addFieldInfo(ORDERID, DWQueryValueOperator.Equals, orderId).addOrderBy(CREATE_DATE);
                DWQueryInfo att_queryInfo = new DWQueryInfo(WORK_ORDER_ATTACHMENT).addFieldInfo(ORDERID, DWQueryValueOperator.Equals, orderId).addOrderBy(CREATE_DATE);

                DWDataSetOperationOption refUserOption = new DWDataSetOperationOption();
                DWQueryInfo user_queryInfo = new DWQueryInfo(WORK_ORDER_USER_MESSAGE).addFieldInfo(ORDERID, DWQueryValueOperator.Equals, orderId).addOrderBy(CREATE_DATE);
                refUserOption.addCascadeQuery(WORK_ORDER_USER_MESSAGE, WORK_ORDER_USER_MESSAGE_ATTACHMENT);
                refUserOption.setTenantEnabled(false);

                DWDataSet assResult = dao.select(ass_queryInfo, refOption);
                DWDataSet attResult = dao.select(att_queryInfo, refOption);
                DWDataSet userResult = dao.select(user_queryInfo, refUserOption);

                primaryRow.set(WORK_ORDER_ASSIGNEE_MESSAGE, assResult.getTable(WORK_ORDER_ASSIGNEE_MESSAGE));
                primaryRow.set(WORK_ORDER_USER_MESSAGE, userResult.getTable(WORK_ORDER_USER_MESSAGE));
                primaryRow.set(WORK_ORDER_ATTACHMENT, attResult.getTable(WORK_ORDER_ATTACHMENT));
            }

            for (DWDataRow primaryRow : result.getTables().getPrimaryTable().getRows()) {
                List<Map<String, Object>> message = new ArrayList<>();
                String orderId = String.format("%07d", Integer.valueOf(primaryRow.get(ORDERID).toString()));
                primaryRow.set(ORDERID, orderId);
                primaryRow.set(SERVICETYPEID, String.format("%05d", Integer.valueOf(primaryRow.get(SERVICETYPEID).toString())));
                primaryRow.set(ORDERTYPEID, String.format("%07d", Integer.valueOf(primaryRow.get(ORDERTYPEID).toString())));

                for (DWDataRow row : (DWDataRowCollection) primaryRow.get(WORK_ORDER_ATTACHMENT)) {
                    row.set(ORDERID, orderId);
                }
                for (DWDataRow row : (DWDataRowCollection) primaryRow.get(WORK_ORDER_ASSIGNEE_MESSAGE)) {
                    row.set(ORDERID, orderId);
                    Map<String, Object> rowData = row.getData();
                    rowData.put(MARK, "assignee");
                    rowData.remove(MODIFY_BY);
                    rowData.remove(MODIFY_BY_ID);
                    rowData.remove(MODIFY_DATE);
                    message.add(row.getData());
                }

                for (DWDataRow row : (DWDataRowCollection) primaryRow.get(WORK_ORDER_USER_MESSAGE)) {
                    row.set(ORDERID, orderId);
                    Map<String, Object> rowData = row.getData();
                    rowData.put(MARK, "user");
                    rowData.remove(MODIFY_BY);
                    rowData.remove(MODIFY_BY_ID);
                    rowData.remove(MODIFY_DATE);
                    message.add(row.getData());
                }

                Collections.sort(message, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        Date date1 = (Date) o1.get(CREATE_DATE);
                        Date date2 = (Date) o2.get(CREATE_DATE);
                        return date1.compareTo(date2);
                    }
                });

                DWDataTableBuilder builder = new DWDataTableBuilder();
                DWDataTable dataTable = builder.setName(MESSAGE).addRowOrgDatas(message).create();
                primaryRow.set(MESSAGE, dataTable);

                result.getSourceMap().remove(WORK_ORDER_ASSIGNEE_MESSAGE);
                result.getSourceMap().remove(WORK_ORDER_USER_MESSAGE);
                result.getSourceMap().remove(WORK_ORDER_ATTACHMENT);
                result.getSourceMap().remove(WORK_ORDER_USER_MESSAGE_ATTACHMENT);
                result.getSourceMap().remove(MESSAGE);
                List<Map<String, Object>> data = (List<Map<String, Object>>) result.getSourceMap().get(WORK_ORDER);
                for (Map<String, Object> dataMap : data) {
                    dataMap.remove(WORK_ORDER_ASSIGNEE_MESSAGE);
                    dataMap.remove(WORK_ORDER_USER_MESSAGE);
                }
            }
        }
        return result;
    }

    //取得工單列表
    @Override
    public Object getList(DWPagableQueryInfo pagableQueryInfo) throws Exception {

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        if (profile.get(TENANTID).equals("99990000")) {
            option.setTenantEnabled(false);
        }

        pagableQueryInfo.setTableName(WORK_ORDER);
        pagableQueryInfo.addOrderBy(CREATE_DATE, false);
        Object result = dao.selectWithPage(pagableQueryInfo, option);

        DWPaginationQueryResult resultList = (DWPaginationQueryResult) result;
        DWDataSet resultDataset = resultList.getDataSet();
        for (DWDataRow row : resultDataset.getTable(WORK_ORDER).getRows()) {
            row.set(ORDERID, String.format("%07d", Integer.valueOf(row.get(ORDERID).toString())));
            row.set(SERVICETYPEID, String.format("%05d", Integer.valueOf(row.get(SERVICETYPEID).toString())));
            row.set(ORDERTYPEID, String.format("%07d", Integer.valueOf(row.get(ORDERTYPEID).toString())));
        }
        return DWServiceResultBuilder.build(result);
    }

    //刪除工單
    @Override
    public Object delete(Integer oid) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }

        Integer[] status = new Integer[]{3, 4};
        Object result;

        if (checkStatus(oid, status, new DWDataSetOperationOption())) {

            DWDataSet dataset = new DWDataSet();
            dataset.newTable(WORK_ORDER).newRow().set(ORDERID, oid).set(DELETED, "Y").set("$state", "U");

            result = dao.execute(dataset);
        } else {
            throw new DWArgumentException("status", "Status can't allowed to delete!");
        }
        return DWServiceResultBuilder.build(result);
    }

    //將工單關閉
    @Override
    public Object putClose(Integer oid) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }
        Integer[] status = new Integer[]{1, 2};
        Object result;

        if (checkStatus(oid, status, new DWDataSetOperationOption())) {

            DWDataSet dataset = new DWDataSet();
            dataset.newTable(WORK_ORDER).newRow().set(ORDERID, oid).set(STATUS, 3).set("$state", "U");

            result = dao.execute(dataset);
        } else {
            throw new DWArgumentException("status", "Status can't allowed to close!");
        }
        return DWServiceResultBuilder.build(result);
    }

    //撤銷工單
    @Override
    public Object putCancel(Integer oid) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }
        Integer[] status = new Integer[]{0, 1};
        Object result;

        if (checkStatus(oid, status, new DWDataSetOperationOption())) {

            DWDataSet dataset = new DWDataSet();
            dataset.newTable(WORK_ORDER).newRow().set(ORDERID, oid).set(STATUS, 4).set("$state", "U");

            result = dao.execute(dataset);
        } else {
            throw new DWArgumentException("status", "Status can't allowed to cancel!");
        }
        return DWServiceResultBuilder.build(result);
    }

    //受理工單按鈕
    @Override
    public Object putAccept(Integer oid, String message) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }
        Integer[] status = new Integer[]{0};
        Object result;

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        if (profile.get(TENANTID).equals("99990000")) {
            option.setTenantEnabled(false);
        }

        if (checkStatus(oid, status, option)) {

            result = dao.update(option, PUT_ACCEPT, oid);

            DWDataSet messageDataset = new DWDataSet();
            messageDataset.newTable(WORK_ORDER_ASSIGNEE_MESSAGE).newRow().set(ORDERID, oid).set(CONTENT, message).set(STATUS, 1);
            postMessageByAssignee(messageDataset);

        } else {
            throw new DWArgumentException("status", "Status can't allowed to accept work order!");
        }
        return DWServiceResultBuilder.build(result);
    }

    //已解決按鈕
    @Override
    public Object putSolved(Integer oid, String message) throws Exception {

        if (oid == null) {
            throw new DWArgumentException("oid", "oid is null or empty!");
        }
        Integer[] status = new Integer[]{0, 1};
        Object result;

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        if (profile.get(TENANTID).equals("99990000")) {
            option.setTenantEnabled(false);
        }

        if (checkStatus(oid, status, option)) {

            result = dao.update(option, PUT_SOLVED, oid);

            DWDataSet messageDataset = new DWDataSet();
            messageDataset.newTable(WORK_ORDER_ASSIGNEE_MESSAGE).newRow().set(ORDERID, oid).set(CONTENT, message).set(STATUS, 2);
            postMessageByAssignee(messageDataset);

        } else {
            throw new DWArgumentException("status", "Status can't allowed to solve work order!");
        }
        return DWServiceResultBuilder.build(result);
    }

    //轉派
    @Override
    public Object putTransfer(DWDataSet dataset) throws Exception {

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        if (profile.get(TENANTID).equals("99990000")) {
            option.setTenantEnabled(false);
        }

        Map<String, Object> data = dataset.getTable(WORK_ORDER).getRow(0).getData();
        List<Object> sqlParams = new ArrayList<Object>();
        sqlParams.add(data.get(SERVICETYPEID));
        sqlParams.add(data.get(SERVICETYPE));
        sqlParams.add(data.get(ORDERTYPEID));
        sqlParams.add(data.get(ORDERTYPE));
        sqlParams.add(data.get(CURRENTASSIGNEE));
        sqlParams.add(data.get(CURRENTASSIGNEEID));
        sqlParams.add(data.get(CURRENTASSIGNEEEMAIL));
        sqlParams.add(data.get(ORDERID));

        Object result = dao.update(option, PUT_TRANSFER, sqlParams.toArray());

        DWPagableQueryInfo pagableQueryInfo = new DWPagableQueryInfo();
        pagableQueryInfo.setPageSize(1);
        pagableQueryInfo.setPageNumber(1);
        pagableQueryInfo.addEqualInfo(ORDERID, dataset.getTable(WORK_ORDER).getRow(0).get(ORDERID));
        DWServicePaginationResult resultList = (DWServicePaginationResult) getList(pagableQueryInfo);
        DWDataSet resultDataset = (DWDataSet) resultList.getData();
        DWDataRow resultRow = resultDataset.getTable(WORK_ORDER).getRow(0);

        StringBuilder email = new StringBuilder();

        List<Object> orderTypeIdList = new ArrayList();
        orderTypeIdList.add(resultRow.get(ORDERTYPEID));
        DWServiceResult serviceTypeResult = (DWServiceResult) workOrderTypeService.getDetailList(orderTypeIdList);
        DWDataSet serviceTypeDataSet = (DWDataSet) serviceTypeResult.getData();

        LocalDateTime today = LocalDateTime.now();
        for (DWDataRow serviceTypeRow : serviceTypeDataSet.getTable(WORK_ORDER_ENGINEER).getRows()) {
            if (serviceTypeRow.getData().size() > 0) {
                Timestamp start_time = serviceTypeRow.get(STARTTIME);
                Timestamp end_time = serviceTypeRow.get(ENDTIME);
                if (serviceTypeRow.get(EMAIL) != null && today.isBefore(end_time.toLocalDateTime()) && today.isAfter(start_time.toLocalDateTime())) {
                    email.append(serviceTypeRow.get(EMAIL).toString() + ";");
                }
            }
        }
        String orderId = String.format("%07d", Integer.valueOf(resultRow.get(ORDERID).toString()));

        Map<String, Object> requestEntity = new HashMap<>();
        requestEntity.put(EVENTID, WORK_ORDER_SUBMIT_ASSIGNEE);
        requestEntity.put(CONTACTS, email);//所有工程人員
        Map<String, Object> messageMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(PREFIX, "*工单转派");
        dataMap.put(ORDERID, orderId);
        dataMap.put(USERNAME, resultRow.get(CREATE_BY_ID));
        dataMap.put(USERID, resultRow.get(CREATE_BY_USER_ID));
        dataMap.put(TENANTID, resultRow.get(TENANTID));
        dataMap.put(TENANTNAME, resultRow.get(TENANTNAME));

        String status = null;
        switch ((Integer) resultRow.get(STATUS)) {
            case 0:
                status = "待受理";
                break;
            case 1:
                status = "受理中";
                break;
            case 2:
                status = "待结单";
                break;
            case 3:
                status = "已结单";
                break;
            case 4:
                status = "已撤单";
                break;
        }

        dataMap.put(STATUS, status);
        dataMap.put(CURRENTASSIGNEE, resultRow.get(CURRENTASSIGNEE));
        dataMap.put(SERVICETYPE, resultRow.get(SERVICETYPE));
        dataMap.put(ORDERTYPE, resultRow.get(ORDERTYPE));
        dataMap.put(ORDERTHEME, resultRow.get(ORDERTHEME));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataMap.put(CREATE_DATE, formatter.format(new Date()));
        dataMap.put(DEVOPSURL, omdevopsUrl + "?" + ORDERID + "=" + orderId);
        dataMap.put(DEVDEVOPSURL, DWModuleConfigUtils.getCurrentModuleProperty("devdevopsUrl"));

        messageMap.put(DATA, dataMap);
        requestEntity.put(MESSAGE, messageMap);

        postMail(requestEntity); //發信給所有工程人員

        return DWServiceResultBuilder.build("put success", result);
    }

    //提交備註
    @Override
    public Object postRemark(DWDataSet dataset) throws Exception {

        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        dataset.getTable(WORK_ORDER_REMARK).getRows().forEach(item -> item.set(CREATE_BY_USER_ID, profile.get(USERID)));

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        Object result = dao.execute(dataset, option);

        return DWServiceResultBuilder.build("post success", result);
    }

    //取得備註
    @Override
    public Object getRemark(Integer oid) throws Exception {

        DWQueryInfo queryInfo = new DWQueryInfo(WORK_ORDER_REMARK).addEqualInfo(ORDERID, oid).addOrderBy(CREATE_DATE, false);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);

        DWDataSet result = dao.select(queryInfo, option);
        result.getTable(WORK_ORDER_REMARK).getRows().forEach(item -> item.set(ORDERID, String.format("%07d", Integer.valueOf(item.get(ORDERID).toString()))));

        return result;
    }

    //工程人員回覆
    @Override
    public Object postMessageByAssignee(DWDataSet dataset) throws Exception {

        Object result = dao.execute(dataset);

        DWPagableQueryInfo pagableQueryInfo = new DWPagableQueryInfo();
        pagableQueryInfo.setPageSize(1);
        pagableQueryInfo.setPageNumber(1);
        pagableQueryInfo.addEqualInfo(ORDERID, dataset.getTable(WORK_ORDER_ASSIGNEE_MESSAGE).getRow(0).get(ORDERID));
        DWServicePaginationResult resultList = (DWServicePaginationResult) getList(pagableQueryInfo);
        DWDataSet resultDataset = (DWDataSet) resultList.getData();
        DWDataRow resultRow = resultDataset.getTable(WORK_ORDER).getRow(0);
        String orderId = String.format("%07d", Integer.valueOf(resultRow.get(ORDERID).toString()));

        if ((Integer) resultRow.get(STATUS) == 0) {
            resultRow.set(STATUS, 1);
            DWDataSetOperationOption option = new DWDataSetOperationOption();
            option.setTenantEnabled(false);
            result = dao.update(option, PUT_ACCEPT, orderId);
        }

        Map<String, Object> requestEntity = new HashMap<>();
        requestEntity.put(EVENTID, WORK_ORDER_REPLY);
        requestEntity.put(CONTACTS, resultRow.get(CURRENTASSIGNEEEMAIL));//當前處理人員
        Map<String, Object> messageMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(PREFIX, "*工单回覆");
        dataMap.put(ORDERID, orderId);
        dataMap.put(USERNAME, resultRow.get(CREATE_BY_ID));
        dataMap.put(TENANTID, resultRow.get(TENANTID));
        dataMap.put(TENANTNAME, resultRow.get(TENANTNAME));
        dataMap.put(USERID, resultRow.get(CREATE_BY_USER_ID));

        String status = null;
        switch ((Integer) resultRow.get(STATUS)) {
            case 0:
                status = "待受理";
                break;
            case 1:
                status = "受理中";
                break;
            case 2:
                status = "待结单";
                break;
            case 3:
                status = "已结单";
                break;
            case 4:
                status = "已撤单";
                break;
        }

        dataMap.put(STATUS, status);
        dataMap.put(ORDERTHEME, resultRow.get(ORDERTHEME));
        dataMap.put(CONTACTUSER, resultRow.get(CONTACTUSER));
        dataMap.put(DEVOPSURL, omdevopsUrl + "?" + ORDERID + "=" + orderId);
        dataMap.put(DEVDEVOPSURL, DWModuleConfigUtils.getCurrentModuleProperty("devdevopsUrl"));
        dataMap.put(CONTACTUSER, resultRow.get(CONTACTUSER));
        dataMap.put(CONTENT, dataset.getTable(WORK_ORDER_ASSIGNEE_MESSAGE).getRow(0).get(CONTENT));
        messageMap.put(DATA, dataMap);
        requestEntity.put(MESSAGE, messageMap);

        postMail(requestEntity); //發信給當前處理人員

        dataMap.put(PREFIX, "工单回覆");
        dataMap.put(DEVOPSURL, consoledevopsUrl + "?" + ORDERID + "=" + orderId);
        requestEntity.put(EVENTID, WORK_ORDER_REPLY);
        requestEntity.put(CONTACTS, resultRow.get(EMAIL));//用戶
        postMail(requestEntity); //發信給用戶

        return DWServiceResultBuilder.build("post success", result);
    }

    //用戶回覆
    @Override
    public Object postMessageByUser(DWDataSet dataset) throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.getInsertOption().getAutoIncrementOption().addSource(WORK_ORDER_USER_MESSAGE, WORK_ORDER_USER_MESSAGE_ATTACHMENT, "id");
        option.setRelatedTableTenantEnabled(false);

        Object result = dao.execute(dataset, option);

        DWPagableQueryInfo pagableQueryInfo = new DWPagableQueryInfo();
        pagableQueryInfo.setPageSize(1);
        pagableQueryInfo.setPageNumber(1);
        pagableQueryInfo.addEqualInfo(ORDERID, dataset.getTable(WORK_ORDER_USER_MESSAGE).getRow(0).get(ORDERID));
        DWServicePaginationResult resultList = (DWServicePaginationResult) getList(pagableQueryInfo);
        DWDataSet resultDataset = (DWDataSet) resultList.getData();
        DWDataRow resultRow = resultDataset.getTable(WORK_ORDER).getRow(0);

        String orderId = String.format("%07d", Integer.valueOf(resultRow.get(ORDERID).toString()));

        Map<String, Object> requestEntity = new HashMap<>();
        requestEntity.put(EVENTID, WORK_ORDER_SUBMIT_ASSIGNEE);
        requestEntity.put(CONTACTS, resultRow.get(CURRENTASSIGNEEEMAIL));//當前處理人員
        Map<String, Object> messageMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(PREFIX, "*工单回覆");
        dataMap.put(ORDERID, orderId);
        dataMap.put(USERNAME, resultRow.get(CREATE_BY_ID));
        dataMap.put(USERID, resultRow.get(CREATE_BY_USER_ID));
        dataMap.put(TENANTID, resultRow.get(TENANTID));
        dataMap.put(TENANTNAME, resultRow.get(TENANTNAME));

        String status = null;
        switch ((Integer) resultRow.get(STATUS)) {
            case 0:
                status = "待受理";
                break;
            case 1:
                status = "受理中";
                break;
            case 2:
                status = "待结单";
                break;
            case 3:
                status = "已结单";
                break;
            case 4:
                status = "已撤单";
                break;
        }

        dataMap.put(STATUS, status);
        dataMap.put(CURRENTASSIGNEE, resultRow.get(CURRENTASSIGNEE));
        dataMap.put(SERVICETYPE, resultRow.get(SERVICETYPE));
        dataMap.put(ORDERTYPE, resultRow.get(ORDERTYPE));
        dataMap.put(ORDERTHEME, resultRow.get(ORDERTHEME));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataMap.put(CREATE_DATE, formatter.format(new Date()));
        dataMap.put(CONTACTUSER, resultRow.get(CONTACTUSER));
        dataMap.put(DEVOPSURL, omdevopsUrl + "?" + ORDERID + "=" + orderId);
        dataMap.put(DEVDEVOPSURL, DWModuleConfigUtils.getCurrentModuleProperty("devdevopsUrl"));
        messageMap.put(DATA, dataMap);
        requestEntity.put(MESSAGE, messageMap);

        postMail(requestEntity); //發信給當前處理人員

        dataMap.put(PREFIX, "工单提交");
        requestEntity.put(EVENTID, WORK_ORDER_SUBMIT_USER);
        requestEntity.put(CONTACTS, resultRow.get(EMAIL));//用戶
        postMail(requestEntity); //發信給用戶

        return DWServiceResultBuilder.build("post success", result);
    }

    //檢查工單狀態
    private boolean checkStatus(Integer oid, Integer[] status, DWDataSetOperationOption option) throws Exception {

        boolean result = false;

        DWQueryInfo queryInfo = new DWQueryInfo(WORK_ORDER).addEqualInfo(ORDERID, oid).addFieldInfo(STATUS, DWQueryValueOperator.In, status);
        DWDataSet checkResult = dao.select(queryInfo, option);

        if (checkResult.getTable(WORK_ORDER).getRows().size() > 0) {
            result = true;
        }
        return result;
    }

    //發送mail
    private void postMail(Map<String, Object> requestEntity) throws Exception {
        String url = DWModuleConfigUtils.getCurrentModuleProperty("emcUrl") + DWModuleConfigUtils.getCurrentModuleProperty("emcMailApi");
        URI uri = DWURIBuilder.create(url).build();
        HttpPost httpPostMethod = new HttpPost(uri);

        // 使用 DWJsonEntity 將其序列化為 JSON 字串
        httpPostMethod.setEntity(new DWJsonEntity(requestEntity));
        httpClient.execute(httpPostMethod, Map.class, DWRequestHeaderUtils::getIamApiRequiredHeaders);
    }
}