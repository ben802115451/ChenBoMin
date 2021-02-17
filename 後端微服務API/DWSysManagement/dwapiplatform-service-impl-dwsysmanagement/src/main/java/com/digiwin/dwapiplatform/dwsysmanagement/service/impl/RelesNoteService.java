package com.digiwin.dwapiplatform.dwsysmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.dwapiplatform.dwsysmanagement.service.IRelesNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.*;

public class RelesNoteService implements IRelesNoteService {

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    // 資料表
    static final String RELEASE_NOTE = "release_note";
    static final String RELEASE_NOTE_FILE_ATTACHMENT = "release_note_file_attachment";

    //欄位名稱
    static final String RELEASE_ID = "releaseid";
    static final String FILE_ID = "fileId";
    static final String USER = "USER";
    static final String RELEASE_DATE = "releaseDate";
    static final String STATUS = "status";
    static final String SUBJECT = "subject";
    static final String CONTENT = "content";
    static final String START_DATE = "startDate";
    static final String END_DATE = "endDate";
    static final String NAME = "name";
    static final String URL = "url";

    //SQL語句
    static final String SELECT_FROM_RELEASE_NOTE = "SELECT * from release_note WHERE 1=1 AND status = 1 AND releaseDate <= ? ORDER BY releaseid DESC";
    static final String SELECT_FROM_RELEASE_NOTE_DETAIL = "SELECT * from release_note WHERE 1 = 1";
    static final String SELECT_FROM_RELEASE_NOTE_FILE = "SELECT * from release_note_file_attachment WHERE 1=1";
    static final String DELETE_FROM_RELEASE_NOTE_FILE = "DELETE from release_note_file_attachment WHERE 1=1";
    static final String STATISTICS_SQL_ORDER_BY_LIMIT = "ORDER BY releaseDate DESC LIMIT ?,?";
    static final String UPDATE_TO_1_SQL = "UPDATE release_note SET `status` = 1 ${mgmtFieldUpdateColumns} WHERE releaseid = ? ";
    static final String UPDATE_TO_0_SQL = "UPDATE release_note SET `status` = 0 ${mgmtFieldUpdateColumns} WHERE releaseid = ? ";

    //新增公告
    @Override
    public Object post(DWDataSet dataset) throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.getInsertOption().getAutoIncrementOption().addSource("release_note", "release_note_file_attachment", "releaseid");
        DWSQLExecutionResult result = dao.execute(dataset, option);

        return DWServiceResultBuilder.build("新增成功", result);
    }

    //修改公告
    @Override
    public Object put(DWDataSet dataset) throws Exception {

        DWSQLExecutionResult result = dao.execute(dataset);

        return DWServiceResultBuilder.build("公告更新成功", result);
    }

    //取得有效公告
    @Override
    public Object getActiveList() throws Exception {
        StringBuilder sql = new StringBuilder();
        Calendar cal = Calendar.getInstance();
        Date datetime = cal.getTime();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(datetime);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        sql.append(SELECT_FROM_RELEASE_NOTE);
        List<Map<String, Object>> result = dao.select(option, SELECT_FROM_RELEASE_NOTE, date);

        return DWServiceResultBuilder.build(result);
    }

    //取得公告清單
    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
        queryInfo.setTableName(RELEASE_NOTE);
        Object result = this.dao.selectWithPage(queryInfo);
        return DWServiceResultBuilder.build(result);
    }

    //取得公告詳情
    @Override
    public Object getDetails(List<Object> oids) throws Exception {

        DWQueryCondition condition = new DWQueryCondition();
        StringBuilder sql = new StringBuilder();
        StringBuilder fileSql = new StringBuilder();

        List<Object> sqlParams = new ArrayList<Object>();

        sql.append(SELECT_FROM_RELEASE_NOTE_DETAIL);
        fileSql.append(SELECT_FROM_RELEASE_NOTE_FILE);
        if (oids.size() > 0) {
            condition.addFieldInfo(RELEASE_ID, DWQueryValueOperator.In, oids.toArray());
            if (condition.getItems().size() > 0) {
                DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
                sql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
                fileSql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL

                sqlParams.addAll(conditionResult.getParametersAsList());
            }
        }
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> result = dao.select(option, sql.toString(), sqlParams.toArray());
        List<Map<String, Object>> resultDetail = dao.select(option, fileSql.toString(), sqlParams.toArray());
        for (int i = 0; i < result.size(); i++) {
            List<Map<String, Object>> dtail = new ArrayList<>();
            for (Map<String, Object> releaseDetail : resultDetail) {
                if (result.get(i).get("releaseid") == releaseDetail.get("releaseid")) {
                    dtail.add(releaseDetail);
                }
            }
            result.get(i).put("dtail", dtail);
        }
        return DWServiceResultBuilder.build(result);
    }

    //刪除公告
    @Override
    public Object delete(List<Object> oids) throws Exception {

        DWDataSetBuilder builder = new DWDataSetBuilder();
        DWDataSet dataset = builder.addTable("release_note").setDeletedOids(oids).createDataSet();

        // 設定連動查詢信息
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.addCascadeDeleting("release_note", "release_note_file_attachment");

        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build(result);

    }

//    //刪除公告檔案詳情
//    @Override
//    public Object deleteDetails(List<Object> oids) throws Exception {
//
//        DWQueryCondition condition = new DWQueryCondition();
//        StringBuilder sql = new StringBuilder();
//        List<Object> sqlParams = new ArrayList<Object>();
//
//        sql.append(DELETE_FROM_RELEASE_NOTE_FILE);
//
//        if (oids.size() > 0) {
//            condition.addFieldInfo(FILE_ID, DWQueryValueOperator.In, oids.toArray());
//            if (condition.getItems().size() > 0) {
//                DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
//                sql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
//
//                sqlParams.addAll(conditionResult.getParametersAsList());
//            }
//        }
//        List<Map<String, Object>> result = dao.select(sql.toString(), sqlParams.toArray());
//        return DWServiceResultBuilder.build("公告檔案刪除成功", result);
//    }

    //上架公告
    @Override
    public Object putActive(String oid) throws Exception {

        if (oid == null || oid.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        List<Object> strsToList = Arrays.asList(oid);
        DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
        DWQueryInfo queryInfo = queryInfoBuilder.setOids(strsToList).setPrimaryKeyName(RELEASE_ID).create();
        queryInfo.setTableName(RELEASE_NOTE);

        DWDataRow row = this.dao.selectOne(queryInfo);

        if (row == null) {
            throw new DWBusinessException(String.format("找不到指定的公告releaseid「%s」", oid));
        }

        if (row.get(STATUS).equals(1)) {
            throw new DWBusinessException("此公告已上架");
        }

        Object result = dao.update(UPDATE_TO_1_SQL, oid);

        return DWServiceResultBuilder.build(result);
    }

    //下架公告
    @Override
    public Object putInactive(String oid) throws Exception {

        if (oid == null || oid.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        List<Object> strsToList = Arrays.asList(oid);
        DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
        DWQueryInfo queryInfo = queryInfoBuilder.setOids(strsToList).setPrimaryKeyName(RELEASE_ID).create();
        queryInfo.setTableName(RELEASE_NOTE);

        DWDataRow row = this.dao.selectOne(queryInfo);
        if (row == null) {
            throw new DWBusinessException(String.format("找不到指定的公告releaseid「%s」", oid));
        }

        if (row.get(STATUS).equals(0)) {
            throw new DWBusinessException("此公告已下架");
        }

        Object result = dao.update(UPDATE_TO_0_SQL, oid);
        return DWServiceResultBuilder.build(result);
    }
}
