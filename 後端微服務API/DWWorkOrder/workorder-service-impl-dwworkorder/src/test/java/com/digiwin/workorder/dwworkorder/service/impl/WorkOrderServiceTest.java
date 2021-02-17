package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.http.client.DWHttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderServiceTest {

    @Tested
    WorkOrderService target;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dao = new DWDaoImpl(queryRunner);

    @Injectable
    DWHttpClient httpClient;

    @Injectable
    WorkOrderTypeService workOrderTypeService;

    DWDataSet dataset;

    DWQueryInfo queryInfo;

    @Mocked
    DWDataSetBuilder mockDWDataSetBuilder;

    @Mocked
    DWMetadataContainer mockDWMetadataContainer;

    @Mocked
    DWDataColumnCollection mockDWDataColumnCollection;

    @Mocked
    DWSQLExecutionResult mockDWSQLExecutionResult;

    @Mocked
    DWDataSetOperationOption mockDWDataSetOperationOption;

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
    static final String USERID = "userId";
    static final String STATUS = "status";
    static final String CREATE_DATE = "create_date";
    static final String SERVICETYPE = "serviceType";
    static final String SERVICETYPEID = "serviceTypeId";
    static final String ORDERTYPEID = "orderTypeId";
    static final String ORDERTYPE = "orderType";
    static final String ORDERTHEME = "orderTheme";
    static final String CURRENTASSIGNEE = "currentAssignee";
    static final String CURRENTASSIGNEEID = "currentAssigneeId";
    static final String CURRENTASSIGNEEEMAIL = "currentAssigneeEmail";
    static final String CONTENT = "content";
    static final String ISPRINCIPAL = "isPrincipal";
    static final String EMAIL = "email";
    static final String ASSIGNEE_NAME = "assignee_name";
    static final String ASSIGNEE_ID = "assignee_id";
    static final String CREATE_BY_ID = "create_by_id";
    static final String CREATE_BY_USER_ID = "create_by_user_id";
    static final String MESSAGE = "message";
    static final String CONTACTUSER = "contactUser";
    static final String STARTTIME = "start_time";
    static final String ENDTIME = "end_time";

    @Test
    void post() throws Exception {

        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow().set(ORDERTYPEID, "0000001").set(SERVICETYPE, "serviceTypeTest")
                .set(ORDERTYPE, "orderTypeTest").set(ORDERTHEME, "orderThemeTest").set(CREATE_DATE, "9999-99-99")
                .set(DWDataRowState.COLUMN_NAME_ROW_STATE, DWDataRowState.CREATE_OPERATION);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(TENANTID, "99990000");
                        return profile;
                    }
                };

                new MockUp<WorkOrderTypeService>() {
                    @Mock
                    public Object getDetailList(List<Object> oids) {
                        Timestamp start_time = Timestamp.valueOf("1111-11-11 00:00:00");
                        Timestamp end_time = Timestamp.valueOf("9999-11-11 00:00:00");

                        DWDataSet serviceTypeDataSet = new DWDataSet();
                        serviceTypeDataSet.newTable(WORK_ORDER_ENGINEER).newRow().set(EMAIL, "digiwinTest@digiwin.com")
                                .set(ISPRINCIPAL, 1).set(ASSIGNEE_NAME, "assigneeNameTest").set(ASSIGNEE_ID, "assigneeIdTest")
                                .set(STARTTIME, start_time).set(ENDTIME, end_time);

                        DWServiceResult serviceTypeResult = new DWServiceResult();
                        serviceTypeResult.setData(serviceTypeDataSet);

                        return serviceTypeResult;
                    }
                };

                dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                result = mockDWSQLExecutionResult;

                List<Object> generatedKeys = new ArrayList<>();
                generatedKeys.add(2);

                mockDWSQLExecutionResult.getGeneratedKeys(WORK_ORDER);
                result = generatedKeys;
            }
        };
        DWServiceResult postResult = (DWServiceResult) target.post(dataset);
        assertTrue(postResult.getSuccess());
        assertEquals("post success", postResult.getMessage());

        //工單類型沒有主要負責人
        new Expectations() {
            {
                new MockUp<WorkOrderTypeService>() {
                    @Mock
                    public Object getDetailList(List<Object> oids) {
                        DWDataSet serviceTypeDataSet = new DWDataSet();
                        serviceTypeDataSet.newTable(WORK_ORDER_ENGINEER);

                        DWServiceResult serviceTypeResult = new DWServiceResult();
                        serviceTypeResult.setData(serviceTypeDataSet);

                        return serviceTypeResult;
                    }
                };
            }
        };
        postResult = (DWServiceResult) target.post(dataset);
        assertTrue(postResult.getSuccess());
        assertEquals("post success", postResult.getMessage());
    }

    @Test
    void get() throws Exception {
        List<Integer> oids = new ArrayList<>();
        oids.add(1);

        new MockUp<DWDataSet>() {
            @Mock
            void $clinit() {
            }
        };
        dataset = new DWDataSet();

        String woString = "[{\"serviceType\":\"testServiceType\",\"orderType\":\"單元測試\",\"orderTypeId\":\"1\"," +
                "\"description\":\"testContent\",\"create_by_id\":\"DAP\",\"modify_by\":\"0\",\"create_by\":\"987654321\"," +
                "\"tenantName\":\"鼎捷软件\",\"tenantSid\":\"99990000\",\"$state\":\"U\",\"currentAssigneeId\":\"currentAssigneeId\"," +
                "\"serviceTypeId\":\"1\",\"create_by_user_id\":\"digiwinTest@digiwin.com\",\"create_date\":\"9999/99/99 00:00:00\"," +
                "\"currentAssigneeEmail\":\"digiwinTest@digiwin.com\",\"email\":\"digiwinTest@digiwin.com\"," +
                "\"defaultAssigneeId\":\"defaultAssigneeId\",\"orderid\":\"1\",\"modify_by_id\":null,\"currentAssignee\":\"鼎新\"," +
                "\"orderTheme\":\"單元測試\",\"phoneNumber\":\"0900000000\",\"deleted\":\"N\",\"defaultAssignee\":\"鼎新\"," +
                "\"modify_date\":null,\"status\":\"0\"}]";

        Gson gson = new GsonBuilder().create();

        List<Map<String, Object>> woList = gson.fromJson(woString, List.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        for (Map<String, Object> woMap : woList) {
            woMap.put(CREATE_DATE, sdf.parse(woMap.get(CREATE_DATE).toString()));
        }
        DWDataTableBuilder builder = new DWDataTableBuilder().setName(WORK_ORDER).addRowOrgDatas(woList);
        dataset = builder.create().getDataSet();

        String attString = "[{\"fileName\":\"testFileName\",\"orderid\":\"0000011\",\"create_by_id\":\"鼎新\",\"modify_by\":\"0\"," +
                "\"modify_by_id\":null,\"create_by\":\"987654321\",\"tenantSid\":\"99990000\",\"$state\":\"U\",\"fileUrl\":\"testFileUrl\"," +
                "\"id\":\"1\",\"create_date\":\"9999/99/99 00:00:00\",\"modify_date\":null,\"fileId\":\"abcde123456\"}]";

        List<Map<String, Object>> attList = gson.fromJson(attString, List.class);
        for (Map<String, Object> attMap : attList) {
            attMap.put(CREATE_DATE, sdf.parse(attMap.get(CREATE_DATE).toString()));
        }
        DWDataTableBuilder attBuilder = new DWDataTableBuilder().setName(WORK_ORDER_ATTACHMENT).addRowOrgDatas(attList);
        DWDataSet attDataset = attBuilder.create().getDataSet();

        String assString = "[{\"create_by\":\"987654321\",\"tenantSid\":\"99990000\",\"$state\":\"U\",\"orderid\":\"0000001\"," +
                "\"create_by_id\":\"鼎新\",\"id\":\"14\",\"create_date\":\"2021/01/14 17:22:54\",\"content\":\"您好, 我们已经收到您提交的问题, 正在为您查看, 请稍等\"," +
                "\"status\":1},{\"create_by\":\"987654321\",\"tenantSid\":\"99990000\",\"$state\":\"U\",\"orderid\":\"0000001\",\"create_by_id\":\"鼎新\"," +
                "\"id\":\"14\",\"create_date\":\"2021/01/14 17:22:54\",\"content\":\"我們已收到問題，盡快為您處理!\",\"status\":null}]";

        List<Map<String, Object>> assList = gson.fromJson(assString, List.class);
        for (Map<String, Object> assMap : assList) {
            assMap.put(CREATE_DATE, sdf.parse(assMap.get(CREATE_DATE).toString()));
        }
        DWDataTableBuilder assBuilder = new DWDataTableBuilder().setName(WORK_ORDER_ASSIGNEE_MESSAGE).addRowOrgDatas(assList);
        DWDataSet assDataset = assBuilder.create().getDataSet();

        String userString = "[{\"create_by\":\"987654321\",\"tenantSid\":\"99990000\",\"$state\":\"U\",\"orderid\":\"0000001\"," +
                "\"create_by_id\":\"鼎新\",\"id\":\"14\",\"create_date\":\"2021/01/14 17:49:26\",\"content\":\"我想詢問關於平台相關問題\",\"status\":null}]";

        List<Map<String, Object>> userList = gson.fromJson(userString, List.class);
        for (Map<String, Object> userMap : userList) {
            userMap.put(CREATE_DATE, sdf.parse(userMap.get(CREATE_DATE).toString()));
        }
        DWDataTableBuilder userBuilder = new DWDataTableBuilder().setName(WORK_ORDER_USER_MESSAGE).addRowOrgDatas(userList);
        DWDataSet userDataset = userBuilder.create().getDataSet();

        DWQueryInfo queryInfo = new DWQueryInfo(WORK_ORDER);
        DWQueryInfo att_queryInfo = new DWQueryInfo(WORK_ORDER_ATTACHMENT);
        DWQueryInfo ass_queryInfo = new DWQueryInfo(WORK_ORDER_ASSIGNEE_MESSAGE);
        DWQueryInfo user_queryInfo = new DWQueryInfo(WORK_ORDER_USER_MESSAGE);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(TENANTID, "99990000");
                        return profile;
                    }
                };
                {
                    dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                    if (queryInfo.getTableName().equals(WORK_ORDER)) {
                        result = dataset;
                    }
                    if (ass_queryInfo.getTableName().equals(WORK_ORDER_ASSIGNEE_MESSAGE)) {
                        result = assDataset;
                    }
                    if (att_queryInfo.getTableName().equals(WORK_ORDER_ATTACHMENT)) {
                        result = attDataset;
                    }
                    if (user_queryInfo.getTableName().equals(WORK_ORDER_USER_MESSAGE)) {
                        result = userDataset;
                    }
                }
                new MockUp<DWDataSet>() {
                    @Mock
                    public Map<String, Object> getSourceMap() {
                        Map<String, Object> map = new HashMap<>();
                        map.put(WORK_ORDER_ASSIGNEE_MESSAGE, WORK_ORDER_ASSIGNEE_MESSAGE);
                        map.put(WORK_ORDER_USER_MESSAGE, WORK_ORDER_USER_MESSAGE);
                        map.put(WORK_ORDER_ATTACHMENT, WORK_ORDER_ATTACHMENT);
                        map.put(WORK_ORDER_USER_MESSAGE_ATTACHMENT, WORK_ORDER_USER_MESSAGE_ATTACHMENT);
                        map.put(MESSAGE, MESSAGE);
                        List<Map<String, Object>> workOrderList = new ArrayList<>();
                        Map<String, Object> workOrderMap = new HashMap<>();
                        workOrderMap.put(WORK_ORDER_ASSIGNEE_MESSAGE, WORK_ORDER_ASSIGNEE_MESSAGE);
                        workOrderMap.put(WORK_ORDER_USER_MESSAGE, WORK_ORDER_USER_MESSAGE);
                        workOrderList.add(workOrderMap);
                        map.put(WORK_ORDER, workOrderList);
                        return map;
                    }
                };
            }
        };
        DWDataSet getResult = target.get(oids);
        assertEquals("0000001", getResult.getTable(WORK_ORDER).getRow(0).get(ORDERID));
        assertEquals("00001", getResult.getTable(WORK_ORDER).getRow(0).get(SERVICETYPEID));
        assertEquals("0000001", getResult.getTable(WORK_ORDER).getRow(0).get(ORDERTYPEID));

    }

    @Test
    void getList() throws Exception {

        DWPagableQueryInfo pagableQueryInfo = new DWPagableQueryInfo(WORK_ORDER);
        DWPaginationQueryResult resultList = new DWPaginationQueryResult(1, 10);

        new MockUp<DWDataSet>() {
            @Mock
            void $clinit() {
            }
        };
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow().set(ORDERID, 1).set(SERVICETYPEID, 1).set(ORDERTYPEID, 1);
        resultList.setDataSet(dataset);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(TENANTID, "99990000");
                        return profile;
                    }
                };

                {
                    dao.selectWithPage(pagableQueryInfo, (DWDataSetOperationOption) any);
                    result = resultList;
                }
            }
        };

        DWServiceResult getListResult = (DWServiceResult) target.getList(pagableQueryInfo);
        DWDataSet dataserResult = (DWDataSet) getListResult.getData();
        assertTrue(getListResult.getSuccess());
        assertEquals("0000001", dataserResult.getTable(WORK_ORDER).getRow(0).get(ORDERID));
        assertEquals("00001", dataserResult.getTable(WORK_ORDER).getRow(0).get(SERVICETYPEID));
        assertEquals("0000001", dataserResult.getTable(WORK_ORDER).getRow(0).get(ORDERTYPEID));
    }

    @Test
    void delete() throws Exception {
        //沒傳oid
        assertThrows(DWArgumentException.class, () -> target.delete(null));

        //傳status非[3,4的工單]
        new MockUp<DWDataSet>() {
            @Mock
            void $clinit() {
            }
        };
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER);

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        assertThrows(DWArgumentException.class, () -> target.delete(0000001));

        //正常更新
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow();

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }

            {
                dao.execute((DWDataSet) any);
                result = mockDWSQLExecutionResult;
            }
        };
        DWServiceResult deleteResult = (DWServiceResult) target.delete(0000001);
        assertTrue(deleteResult.getSuccess());
    }

    @Test
    void putClose() throws Exception {
        //沒傳oid
        assertThrows(DWArgumentException.class, () -> target.putClose(null));

        //傳status非[1,2的工單]
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER);

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        assertThrows(DWArgumentException.class, () -> target.putClose(0000001));

        //正常更新
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow();

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }

            {
                dao.execute((DWDataSet) any);
                result = mockDWSQLExecutionResult;
            }
        };
        DWServiceResult putCloseResult = (DWServiceResult) target.putClose(0000001);
        assertTrue(putCloseResult.getSuccess());
    }

    @Test
    void putCancel() throws Exception {
        //沒傳oid
        assertThrows(DWArgumentException.class, () -> target.putCancel(null));

        //傳status非[0,1的工單]
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER);

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        assertThrows(DWArgumentException.class, () -> target.putCancel(0000001));

        //正常更新
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow();

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }

            {
                dao.execute((DWDataSet) any);
                result = mockDWSQLExecutionResult;
            }
        };
        DWServiceResult putCancelResult = (DWServiceResult) target.putCancel(0000001);
        assertTrue(putCancelResult.getSuccess());
    }

    @Test
    void putAccept() throws Exception {
        Integer oid = 0000001;

        //沒傳oid
        assertThrows(DWArgumentException.class, () -> target.putAccept(null, "受理工單單元測試"));

        // 傳status非[0的工單]
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(TENANTID, "99990000");
                        return profile;
                    }
                };

                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        assertThrows(DWArgumentException.class, () -> target.putAccept(oid, "受理工單單元測試"));

        //正常更新
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow();

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;

                String sql = "UPDATE work_order SET STATUS = '1' ${mgmtFieldUpdateColumns} WHERE orderid = ?";
                dao.update((DWDataSetOperationOption) any, sql, oid);
                result = 1;


                new MockUp<WorkOrderService>() {
                    @Mock
                    public Object postMessageByAssignee(DWDataSet dataset) {
                        return mockDWSQLExecutionResult;
                    }
                };
            }
        };

        DWServiceResult putAcceptResult = (DWServiceResult) target.putAccept(oid, "受理工單單元測試");

        assertTrue(putAcceptResult.getSuccess());
    }

    @Test
    void putSolved() throws Exception {
        Integer oid = 0000001;

        //沒傳oid
        assertThrows(DWArgumentException.class, () -> target.putSolved(null, "已解決工單單元測試"));

        //傳status非[0,1的工單]
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(TENANTID, "99990000");
                        return profile;
                    }
                };

                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        assertThrows(DWArgumentException.class, () -> target.putSolved(oid, "已解決工單單元測試"));

        //正常更新
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow();

        new Expectations() {
            {

                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;

                String sql = "UPDATE work_order SET STATUS = '2' ${mgmtFieldUpdateColumns} WHERE orderid = ?";
                dao.update((DWDataSetOperationOption) any, sql, oid);
                result = 1;

                new MockUp<WorkOrderService>() {
                    @Mock
                    public Object postMessageByAssignee(DWDataSet dataset) {
                        return mockDWSQLExecutionResult;
                    }
                };
            }
        };

        DWServiceResult putSolvedResult = (DWServiceResult) target.putSolved(oid, "已解決工單單元測試");
        assertTrue(putSolvedResult.getSuccess());
    }

    @Test
    void putTransfer() throws Exception {

        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER).newRow().set(ORDERID, 1).set(ORDERTYPEID, 1).set(SERVICETYPEID, 1).set(CREATE_BY_ID, "BominTest")
                .set(CREATE_BY_USER_ID, "ben802115451").set(TENANTID, "99990000").set(TENANTNAME, "鼎新").set(STATUS, 0)
                .set(CURRENTASSIGNEE, "Bomin").set(CURRENTASSIGNEEID, "Bomin").set(CURRENTASSIGNEEEMAIL, "Bomin@digiwin.com")
                .set(SERVICETYPE, "serviceTypeTest").set(ORDERTYPE, "orderTypeTest").set(ORDERTHEME, "orderThemeTest")
                .set(CREATE_DATE, "9999/99/99 00:00:00");

        DWServicePaginationResult resultList = new DWServicePaginationResult();
        resultList.setData(dataset);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(TENANTID, "99990000");
                        return profile;
                    }
                };

                String sql = "UPDATE work_order SET serviceTypeId = ?,  serviceType = ?, orderTypeId = ?," +
                        " orderType = ?,  currentAssignee = ?, currentAssigneeId = ?, currentAssigneeEmail = ? ${mgmtFieldUpdateColumns} WHERE orderid = ?";

                List<Object> sqlParams = new ArrayList<Object>();
                sqlParams.add(1);
                sqlParams.add("serviceTypeTest");
                sqlParams.add(1);
                sqlParams.add("orderTypeTest");
                sqlParams.add("Bomin");
                sqlParams.add("Bomin");
                sqlParams.add("Bomin@digiwin.com");
                sqlParams.add(1);

                dao.update((DWDataSetOperationOption) any, sql, sqlParams.toArray());
                result = 1;

                new MockUp<WorkOrderService>() {
                    @Mock
                    public Object getList(DWPagableQueryInfo pagableQueryInfo) {
                        return resultList;
                    }

                    @Mock
                    public Object postMessageByAssignee(DWDataSet dataset) {
                        return mockDWSQLExecutionResult;
                    }
                };

                new MockUp<WorkOrderTypeService>() {
                    @Mock
                    public Object getDetailList(List<Object> oids) {
                        Timestamp start_time = Timestamp.valueOf("1111-11-11 00:00:00");
                        Timestamp end_time = Timestamp.valueOf("9999-11-11 00:00:00");

                        DWDataSet serviceTypeDataSet = new DWDataSet();
                        serviceTypeDataSet.newTable(WORK_ORDER_ENGINEER).newRow().set(EMAIL, "digiwinTest@digiwin.com")
                                .set(STARTTIME, start_time).set(ENDTIME, end_time);

                        DWServiceResult serviceTypeResult = new DWServiceResult();
                        serviceTypeResult.setData(serviceTypeDataSet);

                        return serviceTypeResult;
                    }
                };
            }
        };

        DWServiceResult putTransferdResult = (DWServiceResult) target.putTransfer(dataset);

        assertTrue(putTransferdResult.getSuccess());
        assertEquals("put success", putTransferdResult.getMessage());
    }

    @Test
    void postRemark() throws Exception {

        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER_REMARK).newRow();

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> profile = new HashMap<String, Object>();
                        profile.put(USERID, "99990000");
                        return profile;
                    }
                };

                {
                    dao.execute(dataset, (DWDataSetOperationOption) any);
                    result = mockDWSQLExecutionResult;
                }
            }
        };
        DWServiceResult postRemarkResult = (DWServiceResult) target.postRemark(dataset);

        assertTrue(postRemarkResult.getSuccess());
        assertEquals("post success", postRemarkResult.getMessage());
    }

    @Test
    void getRemark() throws Exception {
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER_REMARK).newRow().set(ORDERID, 1);

        new Expectations() {
            {
                dao.select((DWQueryInfo) any, (DWDataSetOperationOption) any);
                result = dataset;
            }
        };
        DWDataSet getRemarkResult = (DWDataSet) target.getRemark(0000001);
        assertEquals("0000001", getRemarkResult.getTable(WORK_ORDER_REMARK).getRow(0).get(ORDERID));
    }

    @Test
    void postMessageByAssignee() throws Exception {

        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER_ASSIGNEE_MESSAGE).newRow().set(ORDERID, 1).set(CONTENT, "工程人員回覆單元測試");

        new Expectations() {
            {
                dao.execute((DWDataSet) any);
                result = mockDWSQLExecutionResult;

                new MockUp<WorkOrderService>() {
                    @Mock
                    public Object getList(DWPagableQueryInfo pagableQueryInfo) {
                        DWDataSet getListDataset = new DWDataSet();
                        getListDataset.newTable(WORK_ORDER).newRow().set(ORDERID, 1).set(CURRENTASSIGNEEEMAIL, "bominTest@digiwin.com").set(CREATE_BY_ID, "BominTest")
                                .set(CREATE_BY_USER_ID, "ben802115451").set(STATUS, 0).set(CONTACTUSER, "Bomin")
                                .set(EMAIL, "test@digiwin.com").set(ORDERTYPE, "orderTypeTest").set(ORDERTHEME, "orderThemeTest");

                        DWServicePaginationResult resultList = new DWServicePaginationResult();
                        resultList.setData(getListDataset);
                        return resultList;
                    }
                };
            }
        };

        DWServiceResult postMessageByAssigneeResult = (DWServiceResult) target.postMessageByAssignee(dataset);

        assertTrue(postMessageByAssigneeResult.getSuccess());
        assertEquals("post success", postMessageByAssigneeResult.getMessage());
    }

    @Test
    void postMessageByUser() throws Exception {

        new MockUp<DWDataSet>() {
            @Mock
            void $clinit() {
            }
        };
        dataset = new DWDataSet();
        dataset.newTable(WORK_ORDER_USER_MESSAGE).newRow().set(ORDERID, 1).set(CONTENT, "用戶回覆單元測試");

        new Expectations() {
            {
                dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                result = mockDWSQLExecutionResult;

                new MockUp<WorkOrderService>() {
                    @Mock
                    public Object getList(DWPagableQueryInfo pagableQueryInfo) {
                        DWDataSet getListDataset = new DWDataSet();
                        getListDataset.newTable(WORK_ORDER).newRow().set(ORDERID, 1).set(CURRENTASSIGNEEEMAIL, "bominTest@digiwin.com").set(CREATE_BY_ID, "BominTest")
                                .set(CREATE_BY_USER_ID, "ben802115451").set(STATUS, 0).set(CONTACTUSER, "Bomin")
                                .set(EMAIL, "test@digiwin.com").set(ORDERTHEME, "orderThemeTest");

                        DWServicePaginationResult resultList = new DWServicePaginationResult();
                        resultList.setData(getListDataset);
                        return resultList;
                    }
                };
            }
        };

        DWServiceResult postMessageByUserResult = (DWServiceResult) target.postMessageByUser(dataset);

        assertTrue(postMessageByUserResult.getSuccess());
        assertEquals("post success", postMessageByUserResult.getMessage());
    }
}
