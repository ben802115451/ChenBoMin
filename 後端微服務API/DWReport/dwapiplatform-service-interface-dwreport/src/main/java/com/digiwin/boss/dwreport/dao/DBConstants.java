package com.digiwin.boss.dwreport.dao;

final public class DBConstants {

    public static final String FROM_TENANT_SUBSCRIPTION_TRANSACTION_STATUS = "from `order` AS m "
            + "left join `order_detail` AS d1 ON (m.sid = d1.order_sid) "
            + "where 1 = 1";

    public static final String SELECT_COUNT = "select count(m.sid) AS `total`";

    public static final String SELECT_FILEDS = "select m.tenantId AS tenantId, m.tenantName AS tenantName, d1.goodsCode AS goodsCode, d1.goodsname AS goodsName, d1.goods_sid AS goodsId, d4.name AS strategyName, m.ordercode AS ordercode, m.create_date AS createTime, m.order_status AS orderStatus, m.totalprice AS amount, m.sid AS sid, m.payprice AS payPrice";

    public static final String FROM_TENANT_SUBSCRIPTION = "from `order` AS m "
            + "left join `order_detail` AS d1 ON (m.sid = d1.order_sid) "
            + "left join `sellingstrategy` AS d4 ON (d1.strategy_sid = d4.sid) "
            + "where 1 = 1";

    public static final String SQL_LIMIT = "LIMIT ?, ?";

    public static final String SQL_ORDER_BY = "order by m.tenantid asc, m.create_date asc";

    public static final String SQL_SELECT_COUNT = SELECT_COUNT + " " + FROM_TENANT_SUBSCRIPTION;

    public static final String SQL_SELECT_TRANSACTION_STATUS = SELECT_COUNT + " " + FROM_TENANT_SUBSCRIPTION_TRANSACTION_STATUS;

    public static final String SQL_SELECT_FILEDS = SELECT_FILEDS + " " + FROM_TENANT_SUBSCRIPTION;

    public static final String SQL_WHERE_TENANTID_TENANTNAME = "AND (m.tenantid LIKE ? OR m.tenantname LIKE ?)";

    public static final String SQL_WHERE_PRODUCT_TYPE = "AND (m.categoryid = ? OR m.categoryid = ?)";

    public static final String SQL_WHERE_GOODS_CODE_NAME = "AND (d1.goodsCode = ? OR d1.goodsname LIKE ?)";

    public static final String SQL_WHERE_STRATEGY_NAME = "AND (d4.name LIKE ?)";

    public static final String SQL_WHERE_ORDER_STATUS_FIXED = "AND (m.order_status = ?)";

//	public static final String SQL_WHERE_ORDER_STATUS = "AND (d2.orderstatus = ? or d2.orderstatus = ?)";

    public static final String SQL_WHERE_BEGIN_TIME = "AND (m.create_date >= ?)";

    public static final String SQL_WHERE_END_TIME = "AND (m.create_date <= ?)";

    public static final String SQL_WHERE_SID = "AND (m.sid <> ?)";

    public static final String SQL_WHERE_TENANTID = "AND (m.tenantid = ?)";

    public static final String SQL_WHERE_GOODS_CODE = "AND (d1.goodsCode = ?)";

    public static final String SQL_WHERE_GOODS_CODE_IN = "AND (d1.goodsCode IN (@args@))";

    public static final String SQL_WHERE_CREATETIME = "AND (m.create_date < ?)";

    public static final String SELECT_FROM_SALES_STATISTICS = "SELECT *FROM sales_statistics WHERE 1=1";

    public static final String SELECT_CASE_FROM_SALES_STATISTICS = "SELECT orderSource,payDate,categoryId,goodsCode,goodsName,strategyCode,strategyName,orderMode,orderCount,quantity,payPrice, CASE WHEN payMethod = 'Offline' THEN '0' WHEN payMethod <> 'Offline' THEN '1' END AS payMethod FROM sales_statistics WHERE 1=1";

    public static final String SELECT_FROM_SALES_STATISTICS_TOTAL = "SELECT SUM(payPrice) AS totalPayPrice,ROUND(SUM(payPrice) / SUM(orderCount),2) AS averageUnitPayPrice,SUM(orderCount) AS totalOrderCount FROM sales_statistics WHERE 1=1 AND orderMode = 1";

    public static final String SELECT_FROM_SALES_STATISTICS_MONTHTOTAL = "SELECT DAY(payDate) AS payDate,SUM(payPrice) AS totalPayPrice,ROUND(SUM(payPrice) / SUM(orderCount),2) AS averageUnitPayPrice,SUM(quantity) AS totalQuantity,SUM(orderCount) AS totalOrderCount FROM sales_statistics WHERE payDate LIKE ? AND orderMode = 1";

    public static final String SELECT_FROM_SALES_STATISTICS_YEARTOTAL = "SELECT MONTH(payDate) AS payDate,SUM(payPrice) AS totalPayPrice,ROUND(SUM(payPrice) / SUM(orderCount),2) AS averageUnitPayPrice,SUM(quantity) AS totalQuantity,SUM(orderCount) AS totalOrderCount FROM sales_statistics WHERE payDate LIKE ? AND orderMode = 1";

    public static final String SALES_STATISTICS_GROUP_BY_DAY_PAY_DATE = "GROUP BY DAY(payDate)";

    public static final String SALES_STATISTICS_GROUP_BY_MONTH_PAY_DATE = "GROUP BY MONTH(payDate)";

    public static final String STATISTICS_SQL_ORDER_BY_LIMIT = "ORDER BY payDate DESC LIMIT ?,?";

    public static final String DELETE_FROM_SALES_STATISTICS = "DELETE FROM sales_statistics WHERE payDate = ?";

    public static final String INSERT_INTO_SALES_STATISTICS = "INSERT INTO sales_statistics VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String SELECT_FROM_SALES_STATISTICS_GROUP_BY_FIELD = "SELECT goodsName,strategyName,SUM(quantity) AS quantity,SUM(payPrice) AS payPrice FROM sales_statistics WHERE 1 = 1 AND orderMode = 1";

    public static final String SELECT_FROM_SALES_STATISTICS_GROUP_BY_FIELD_STARTDATE = "SELECT goodsName,strategyName,SUM(quantity) AS quantity,SUM(payPrice) AS payPrice FROM sales_statistics WHERE '%s' <= payDate ";

    public static final String SELECT_FROM_SALES_STATISTICS_GROUP_BY_FIELD_ENDDATE = "SELECT goodsName,strategyName,SUM(quantity) AS quantity,SUM(payPrice) AS payPrice FROM sales_statistics WHERE  '%s' >= payDate ";

    public static final String SELECT_FROM_SALES_STATISTICS_GROUP_BY_FIELD_DATE = "SELECT goodsName,strategyName,SUM(quantity) AS quantity,SUM(payPrice) AS payPrice FROM sales_statistics WHERE '%s' <= payDate AND '%s' >= payDate ";

    public static final String SELECT_FROM_SALES_STATISTICS_TOTAL_FILED = "SELECT SUM(%s) AS total FROM sales_statistics WHERE 1 = 1 AND orderMode = 1";

    public static final String SELECT_FROM_SALES_STATISTICS_GROUP_BY = "GROUP BY strategyCode ORDER by %s DESC LIMIT 0, %s";

    //表名
    public static final String EXCLUDE_ORDER = "exclude_order";

    //排除統計的訂單欄位名稱
    public static final String ORDER_CODE = "orderCode";
    public static final String REASON = "reason";

}
