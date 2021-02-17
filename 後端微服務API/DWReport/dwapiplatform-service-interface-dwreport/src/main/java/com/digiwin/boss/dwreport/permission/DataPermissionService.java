package com.digiwin.boss.dwreport.permission;

import com.digiwin.app.dao.DWSqlInfo;
import com.digiwin.data.permission.DWRowPermissionDefaultMatchOption;
import com.digiwin.data.permission.DWRowPermissionMatchOption;
import com.digiwin.data.permission.DWUserPermission;
import com.digiwin.data.service.DWDataPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPermissionService {

    static final String MODULEID = "boss-goods";
    static final String ACTIONID = "boss-goods-management";

    public static Map<String, Object> getDataPermissionMap() throws Exception {

        DWSqlInfo sqlInfo = null;
        Map<String, Object> sqlInfoResult = new HashMap<String, Object>();

            DWUserPermission userPermission = (DWUserPermission) new DWDataPermission().getDataPermission(MODULEID,ACTIONID);

            DWRowPermissionMatchOption option = new DWRowPermissionDefaultMatchOption();

            option.addFilterFieldMapping("goodsCode", "d1.goodsCode");
            sqlInfo = userPermission.getRowPermission().getSQL(option);

            String partialSql = sqlInfo.getSql();
            partialSql = (partialSql.isEmpty()) ? "" : "AND " + "(" + partialSql + ")";
            List parametersAsList = sqlInfo.getParametersAsList();

            sqlInfoResult.put("Superadmin", userPermission.isSuperadmin());
            sqlInfoResult.put("HaveData", userPermission.hasData());
            sqlInfoResult.put("sql", partialSql);
            sqlInfoResult.put("goodsCode", parametersAsList);
            sqlInfoResult.put("userPermission", userPermission);

        return sqlInfoResult;
    }
}
