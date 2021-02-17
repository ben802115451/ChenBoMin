package com.digiwin.dwapiplatform.dwsysmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.dwapiplatform.dwsysmanagement.service.IAnnouncementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Miko
 */
public class AnnouncementService implements IAnnouncementService {
    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    private static final Log log = LogFactory.getLog(AnnouncementService.class);

    // 資料表
    static final String ANNOUNCEMENT_INFO = "announcement_info";
    static final String DISPLAY_PAGE = "display_page";

    //欄位名稱
    static final String ID = "id";
    static final String SUBJECT = "subject";
    static final String CREATE_DATE = "createDate";
    static final String START_DATE = "startDate";
    static final String END_DATE = "endDate";
    static final String STATUS = "status";
    static final String DESCRIPTION = "description";
    static final String PAGE_ID = "pageId";

    //SQL語句
    static final String SELECT_ID_ORDERBY_SQL = "SELECT * FROM announcement_info ORDER BY id DESC LIMIT 0 , 1 -${mgmtField} ";
    static final String DELETE_ID_SQL = "DELETE FROM announcement_info WHERE id = ? ";
    static final String UPDATE_TO_1_SQL = "UPDATE announcement_info SET `status` = 1 ${mgmtFieldUpdateColumns} WHERE id = ? ";
    static final String UPDATE_TO_0_SQL_BY_STATUS = "UPDATE announcement_info SET `status` = 0 ${mgmtFieldUpdateColumns} WHERE `status` = 1 AND pageId = ?";
    static final String UPDATE_TO_0_SQL = "UPDATE announcement_info SET `status` = 0 ${mgmtFieldUpdateColumns} WHERE id = ? ";

    static final String UPDATE_DISPLAY_NAME = "UPDATE display_page SET displayName = ? ${mgmtFieldUpdateColumns} WHERE pageId = ? ";
    static final String SELECT_GOODS_CODE = "SELECT * FROM display_page WHERE goodsCode = ? -${mgmtField} ";
    static final String DELETE_DISPLAY_PAGE = "DELETE FROM display_page WHERE pageId = ? ";
    static final String DELETE_ANNOUNCEMENT_SQL = "DELETE FROM announcement_info WHERE pageId = ? ";
    static final String SELECT_GOODSCODE_AND_DISPLAYID = "-${mgmtField} SELECT pageId FROM display_page WHERE goodsCode = ? AND displayId = ? ";
    static final String SHOW_ANNOUNCEMENT_SQL = "-${mgmtField} SELECT * FROM announcement_info WHERE pageId = ? AND `status` = 1 AND startDate <= ? AND endDate >= ? ";

    //新增公告
    @Override
    public Object post(DWDataSet dataset) throws Exception {

        DWDataTable table = dataset.getTable(ANNOUNCEMENT_INFO);
        for (DWDataRow row : table.getRows()) {
            this.checkAnnouncementInfo(row);
        }

        //從DB裡面抓流水碼 看ID是null還是有資料 string format取最後四碼+1
        Calendar cal = Calendar.getInstance();
        Date systemMonthOfYear = cal.getTime();
        String currentMonthOfYear = new SimpleDateFormat("yyyyMM").format(systemMonthOfYear);

        List<Map<String, Object>> idMap = (List<Map<String, Object>>) this.dao.select(SELECT_ID_ORDERBY_SQL);
        String compareId = "0000000000";
        if (idMap.size() > 0) compareId = idMap.get(0).get(ID).toString();

        int serialNumber = 1;

        if (compareId.length() == 10 && compareId.startsWith(currentMonthOfYear)) {
            serialNumber = Integer.parseInt(compareId.substring(7, 10)) + 1;
        }

        for (DWDataRow row : table.getRows()) {
            if (row.isNew()) {
                row.set(ID, (currentMonthOfYear + String.format("%04d", serialNumber++)));
                row.set(CREATE_DATE, systemMonthOfYear);
            }
        }

        DWSQLExecutionResult result = dao.execute(dataset);

        return DWServiceResultBuilder.build("新增成功", result);
    }

    @Override
    public Object put(DWDataSet dataset) throws Exception {
        Object result = this.dao.execute(dataset);
        return DWServiceResultBuilder.build("公告更新成功", result);
    }

    @Override
    public Object getActiveAnnouncement(String goodsCode, String displayId) throws Exception {
        Object result;
        List<String> emptyList = Collections.emptyList();
        Object getId = this.dao.select(SELECT_GOODSCODE_AND_DISPLAYID, goodsCode, displayId);
        List<Map<String, String>> list = (List<Map<String, String>>) getId;
        if (list.size() == 0) {
            result = emptyList;
        } else {
            String pageId = String.valueOf(list.get(0).get(PAGE_ID));
            Calendar cal = Calendar.getInstance();
            Date systemMonthOfYear = cal.getTime();

            //檢查有效日期是否在今天
            result = this.dao.select(SHOW_ANNOUNCEMENT_SQL, pageId, systemMonthOfYear, systemMonthOfYear);
            List<Map<String, String>> dateList = (List<Map<String, String>>) result;
            if (dateList.size() == 0) {
                result = emptyList;
            }
        }
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
        queryInfo.setTableName(ANNOUNCEMENT_INFO);
        Object result = this.dao.selectWithPage(queryInfo);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object delete(String id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.update(option, DELETE_ID_SQL, id);
        return DWServiceResultBuilder.build("公告刪除成功", result);

    }

    @Override
    public Object putActive(String id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        List<Object> strsToList = Arrays.asList(id);
        DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
        DWQueryInfo queryInfo = queryInfoBuilder.setOids(strsToList).setPrimaryKeyName(ID).create();
        queryInfo.setTableName(ANNOUNCEMENT_INFO);

        DWDataRow row = this.dao.selectOne(queryInfo);
        if (row == null) {
            throw new DWBusinessException(String.format("找不到指定的公告ID「%s」", id));
        }

        if (row.get(STATUS).equals(1)) {
            throw new DWBusinessException("此公告已上架");
        }

        Integer pageId = row.get("pageId");

        dao.update(UPDATE_TO_0_SQL_BY_STATUS, pageId);
        Object result = dao.update(UPDATE_TO_1_SQL, id);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object putInactive(String id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new DWArgumentException("id", "id is null or empty!");
        }

        List<Object> strsToList = Arrays.asList(id);
        DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
        DWQueryInfo queryInfo = queryInfoBuilder.setOids(strsToList).setPrimaryKeyName(ID).create();
        queryInfo.setTableName(ANNOUNCEMENT_INFO);

        DWDataRow row = this.dao.selectOne(queryInfo);
        if (row == null) {
            throw new DWBusinessException(String.format("找不到指定的公告ID「%s」", id));
        }

        if (row.get(STATUS).equals(0)) {
            throw new DWBusinessException("此公告已下架");
        }

        Object result = dao.update(UPDATE_TO_0_SQL, id);
        return DWServiceResultBuilder.build(result);
    }

    @Override
    public Object postDisplayPage(DWDataSet dataset) throws Exception {
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        DWDataSetSqlInfo datasqlInfo = ((DWDaoImpl) (this.dao)).getDialect().parse(dataset, option);
        log.error("postDisplayPage" + datasqlInfo.get(0).getMetadata().getAutoIncrement());
        Object result = this.dao.execute(dataset);
        DWDataTable table = dataset.getTable(DISPLAY_PAGE);
        for (DWDataRow row : table.getRows()) {
            row.set("pageId", row.get("pageid"));
            row.getData().remove("pageid");
        }

        return DWServiceResultBuilder.build("頁面新增成功", dataset);
    }

    @Override
    public Object putDisplayName(String displayName, String pageId) throws Exception {
        Object result = this.dao.update(UPDATE_DISPLAY_NAME, displayName, pageId);
        return DWServiceResultBuilder.build("顯示名稱更新成功", result);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public Object deleteDisplayPage(String pageId) throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        dao.update(option, DELETE_DISPLAY_PAGE, pageId);
        Object result = dao.update(option, DELETE_ANNOUNCEMENT_SQL, pageId);
        return DWServiceResultBuilder.build("頁面刪除成功", result);
    }

    @Override
    public Object getDisplayPageList(String goodsCode) throws Exception {
        List<Map<String, Object>> result = this.dao.select(SELECT_GOODS_CODE, goodsCode);

        return DWServiceResultBuilder.build(result);
    }

    /**
     * 檢查AnnouncementInfo表
     *
     * @param row
     * @throws Exception
     */
    public void checkAnnouncementInfo(DWDataRow row) throws Exception {

        if (row.isDeleted())
            return;

        String subject = (String) row.get(SUBJECT);
        if (subject == null || subject.isEmpty()) {

            throw new DWArgumentException("subject", "subject is null or empty!");
        }

        String startDate = (String) row.get(START_DATE);
        if (startDate == null || startDate.isEmpty()) {
            throw new DWArgumentException("startDate", "startDate is null or empty!");
        }

        String endDate = (String) row.get(END_DATE);
        if (endDate == null || endDate.isEmpty()) {
            throw new DWArgumentException("endDate", "endDate is null or empty!");
        }

        String description = (String) row.get(DESCRIPTION);
        if (description == null || description.isEmpty()) {
            throw new DWArgumentException("description", "description is null or empty!");
        }
    }
}
