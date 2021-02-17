package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.marketmanagement.dwmarketmanagement.service.IMediaAreaService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaAreaService implements IMediaAreaService {

    private static final Log log = LogFactory.getLog(MediaAreaService.class);

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    static final String TABLE = "layout_arrangement";
    static final String LASTMODIFYUSERID = "lastModifyUserId";
    static final String LASTMODIFYUSERNAME = "lastModifyUserName";
    static final String AREATYPE = "areaType";
    static final String DELETE_LAYOUT_ARRANGEMENT = "DELETE FROM layout_arrangement WHERE areaType LIKE 'M-%'";

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object post(DWDataSet dataset) throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataset.getSourceMap().get("layout_arrangement");

        DWSQLExecutionResult result = new DWSQLExecutionResult();

        if (dataList.size() != 0) {
            if (dataList.get(0).containsKey(AREATYPE)) {
                String areaType = (String) dataList.get(0).get(AREATYPE);
                if (areaType.equals("V") || areaType.equals("A")) {

                    dao.update(option, DELETE_LAYOUT_ARRANGEMENT); //刪除原始資料

                    Map<String, Object> profile = DWServiceContext.getContext().getProfile(); // 取得調用的"userId"以及"userName"
                    String lastModifyUserId = (String) profile.get("userId");
                    String lastModifyUserName = (String) profile.get("userName");

                    dataset.getTable(TABLE).getRow(0).set(LASTMODIFYUSERID, lastModifyUserId);
                    dataset.getTable(TABLE).getRow(0).set(LASTMODIFYUSERNAME, lastModifyUserName);
                    dataset.getTable(TABLE).getRow(0).set(AREATYPE, "M-" + areaType);

                    result = dao.execute(dataset, option);
                } else {
                    throw new DWArgumentException("post", "areaType請輸入V or A");
                }
            } else {
                throw new DWArgumentException("post", "請輸入areaType");
            }
        }
        return DWServiceResultBuilder.build("視頻或活動新增成功", result);
    }

    @Override
    public Object get() throws Exception {

        DWQueryInfo queryInfo = new DWQueryInfo();
        queryInfo.setTableName(TABLE);
        queryInfo.addFieldInfo(AREATYPE, DWQueryValueOperator.Like, "M-%");

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        DWDataRow result = (DWDataRow) dao.selectOne(queryInfo, option);
        Map<String, Object> data = new HashMap<>();

        if (result != null) {
            data = result.getData();
            if (data.containsKey("areaType")) {
                String areaType = data.get("areaType").toString();
                areaType = areaType.substring(2);
                data.put("areaType", areaType);
            }
        }
        return DWServiceResultBuilder.build(true, data);
    }
}
