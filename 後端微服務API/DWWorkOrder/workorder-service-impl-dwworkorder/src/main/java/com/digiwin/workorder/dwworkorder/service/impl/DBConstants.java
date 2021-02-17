package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.utils.DWTenantUtils;

/**
 * data base constants
 *
 * @author falcon
 */
final class DBConstants {

    static final String tenantSqlTag = DWTenantUtils.getTenantTagByColumnName();  //default:${tenantsid}

    // 資料表
    static final String WORK_ORDER_TYPE = "work_order_type";
    static final String WORK_ORDER_SERVICE_TYPE = "work_order_service_type";
    static final String WORK_ORDER_ENGINEER = "work_order_engineer";

    // 欄位
    // 工單服務別表
    static final String SERVICE_NAME = "service_name";
    static final String SERVICE_ID = "service_id";
    static final String SEQUENCE = "sequence";

    //工單類型表
    static final String WORK_TYPE_NAME = "type_name";
    static final String WORK_TYPE_ID = "type_id";
    static final String SERVICE_TYPE = "service_type";
    static final String SERVICE_TYPE_ID = "service_type_id";
    static final String CREATE_BY_USERID = "create_by_userId";
    static final String ENGINEER = "assignee";

    // 工程人員表
    static final String MAIL = "email";
    static final String ENGINEER_NAME = "assignee_name";
    static final String ENGINEER_ID = "assignee_id";
    static final String START_TIME = "start_time";
    static final String END_TIME = "end_time";
    static final String IS_PRINCIPAL = "isPrincipal";

    public static final String SELECT_FROM_WORK_ORDER_ENGINEER = " SELECT count('isPrincipal') FROM work_order_engineer WHERE isPrincipal = '1' AND  type_id = ? ";
    public static final String UPDATE_TO_UNPRINCIPAL = " UPDATE work_order_engineer SET isPrincipal = '0' ${mgmtFieldUpdateColumns} WHERE type_id = ? ";
    public static final String UPDATE_TO_PRINCIPAL = " UPDATE work_order_engineer SET isPrincipal = '1' ${mgmtFieldUpdateColumns} WHERE type_id = ? AND assignee_id = ? ";

    //for工單類型查詢用SQL
    public static final String SELECT_ORDER_TYPE_AND_ENGINEER =
            " SELECT order_type.service_type_id, order_type.service_type, " +
                    "        order_type.type_id, order_type.type_name, order_type.sequence, " +
                    "        order_type.create_by_id, order_type.create_by_userId, order_type.create_date, " +
                    "        engineer.assignee_id, engineer.assignee_name, engineer.email, engineer.isPrincipal " +
                    "        FROM work_order_type AS order_type JOIN work_order_engineer AS engineer " +
                    "             ON order_type.type_id = engineer.type_id WHERE 1=1 ";

    //for工單類型查詢用SQL
    public static final String SELECT_ORDER_TYPE_AND_ENGINEER_2 =
            " SELECT order_type.service_type_id, order_type.service_type, " +
                    "        order_type.type_id, order_type.type_name, order_type.sequence, " +
                    "        order_type.create_by_id, order_type.create_by_userId, order_type.create_date " +
                    "        FROM work_order_type AS order_type WHERE 1=1 ";

    public static final String SELECT_WORK_ORDER_TYPE_ALL_DATA = " SELECT * FROM work_order_type " + tenantSqlTag;

    public static final String SQL_ORDER_BY = "order by m.tenantid asc, m.create_date asc";
    public static final String SQL_LIMIT = "LIMIT ?, ?";

    public static final String GROUP_BY = " GROUP BY order_type.type_id ";

    //for別名用
    public static final String WORK_ORDER_TYPE_ALIAS = "order_type.";
    public static final String WORK_ORDER_ENGINEER_ALIAS = "engineer.";
}
