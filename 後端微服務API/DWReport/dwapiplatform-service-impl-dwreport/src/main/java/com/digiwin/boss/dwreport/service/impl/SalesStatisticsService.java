package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.DWContainerContext;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.boss.dwreport.dao.DBConstants;
import com.digiwin.boss.dwreport.permission.DataPermissionService;
import com.digiwin.boss.dwreport.service.ISalesStatisticsService;
import com.digiwin.data.permission.DWRowPermissionIncludedMatchOption;
import com.digiwin.data.permission.DWUserPermission;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SalesStatisticsService implements ISalesStatisticsService {
    private static final Log log = LogFactory.getLog(SalesStatisticsService.class);

    @Autowired
    private ExcludeOrderService excludeOrderService;

    @Autowired
    private SalesStatisticsHistoryService salesStatisticsHistory;

    @Autowired
    private SalesStatisticsService proxy;

    @Autowired
    private DWContainerContext containerContext;
    //統計欄位名稱
    static final String ORDER_SOURCE = "orderSource";
    static final String PAY_DATE = "payDate";
    static final String CATEGORY_ID = "categoryId";
    static final String GOODS_CODE = "goodsCode";
    static final String GOODS_NAME = "goodsName";
    static final String START_DATE = "startDate";
    static final String END_DATE = "endDate";
    static final String STRATEGY_CODE = "strategyCode";
    static final String STRATEGY_NAME = "strategyName";
    static final String ORDER_MODE = "orderMode";
    static final String ORDER_COUNT = "orderCount";
    static final String ORDER_CODE = "orderCode";
    static final String QUANTITY = "quantity";
    static final String PAY_PRICE = "payPrice";
    static final String PAY_METHOD = "payMethod";
    static final String OFFLINE = "Offline";

    //歷程欄位名稱
    static final String UPDATE_DATE = "updateDate";
    static final String UPDATE_STATUS = "updateStatus";
    static final String UPDATE_COUNT = "updateCount";
    static final String UPDATE_ID = "updateID";
    static final String UPDATE_NAME = "updateName";

    //統計圖表欄位名稱
    static final String TOTAL_PAY_PRICE = "totalPayPrice";
    static final String AVERAGE_UNIT_PAY_PRICE = "averageUnitPayPrice";
    static final String TOTAL_QUANTITY = "totalQuantity";
    static final String TOTAL_ORDER_COUNT = "totalOrderCount";

    //SQL
    static final String COUNT_TOTAL_SQL = "SELECT " +
            " SUM(orderCount) totalOrderCount, SUM(payPrice) totalPayPrice, " +
            " ROUND(SUM(payPrice)/SUM(orderCount), 2)  averageUnitPayPrice " +
            " FROM sales_statistics where payDate like ? AND orderMode = '1' ";

    static final String SHOW_YEAR_INFO_SQL = "SELECT payDate, " +
            " SUM(quantity) totalQuantity, " +
            " SUM(orderCount) totalOrderCount, SUM(payPrice) totalPayPrice," +
            " ROUND(SUM(payPrice)/SUM(orderCount), 2) averageUnitPayPrice" +
            " from sales_statistics " +
            " WHERE payDate BETWEEN ? AND ? AND orderMode = '1' ";

    @Autowired
    @Qualifier("bossDao")
    private DWDao dao;

    @Override
    public Object getAssignedDate(Map<String, Object> params) throws Exception {
        String module = DWServiceContext.getContext().getModuleName();
        String omcUrl = DWModuleConfigUtils.getCurrentModuleProperty("omcUrl");
        String payApi = DWModuleConfigUtils.getProperty(module, "payApi");

        // header
        DWServiceContext context = DWServiceContext.getContext();
        Map<String, Object> requestHeader = context.getRequestHeader();
        String userToken = (String) requestHeader.get("token");

        Gson gson = new Gson();
        String paramJson = gson.toJson(params);
        String encodedParams = URLEncoder.encode(paramJson, "UTF-8"); // 棄用了要記得調整

        /**
         * 調用OMC api http://192.168.9.27:22614/api/omc/v2/orders/pay
         * POST
         */
        String payUrl = String.format("%s%s?params=%s", omcUrl, payApi, encodedParams);
        log.info(">>>get pay url = " + payUrl);
        JSONObject json = new JSONObject(params);
        StringEntity assignedDateEntity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        HttpClient assignedDateEntityClient = HttpClientBuilder.create().build();
        HttpPost assignedDateRequest = new HttpPost(payUrl);
        assignedDateRequest.setHeader("Content-Type", "application/json");
        assignedDateRequest.setHeader("digi-middleware-auth-user", userToken);
        assignedDateRequest.setEntity(assignedDateEntity);
        HttpResponse assignedDateResponse = assignedDateEntityClient.execute(assignedDateRequest);
        String assignedDateContent = EntityUtils.toString(assignedDateResponse.getEntity());
        assignedDateContent = "{ \"list\" :" + assignedDateContent + "}";
        int assignedDateStatusCode = assignedDateResponse.getStatusLine().getStatusCode();
        if (assignedDateStatusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get SalesStatisticsService List failed, status code = ").append(assignedDateStatusCode)
                    .append(", please check the log for more information.");

            log.error("ISalesStatisticsService.getAssignedDate failed! -> " + assignedDateContent);

            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> resultMap = (Map<String, Object>) DWGsonProvider.getGson().fromJson(assignedDateContent, Map.class);
        List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("list");
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<String> orderNoList = new ArrayList<>();
        DWServiceResult result = (DWServiceResult) excludeOrderService.get();
        DWDataSet dataset = (DWDataSet) result.getData();
        DWDataTable dataTable = dataset.getTable(DBConstants.EXCLUDE_ORDER);

        for (DWDataRow row : dataTable.getRows()) {
            String orderNo = row.get(DBConstants.ORDER_CODE);
            orderNoList.add(orderNo);
        }

        //從list拿要groupby的欄位，如果值一樣就做相加，不一樣就new
        for (Map<String, Object> tmpMap : list) {
            if (orderNoList.contains(tmpMap.get(ORDER_CODE))) {
                continue;
            }
            String apiPayDate = tmpMap.get(PAY_DATE).toString();
            String apiGoodsCode = tmpMap.get(GOODS_CODE).toString();
            String apiStrategyCode = tmpMap.get(STRATEGY_CODE).toString();
            String apiOrderMode = String.valueOf(tmpMap.get(ORDER_MODE));
            String apiPayMethod = String.valueOf(tmpMap.get(PAY_METHOD));
            /**
             * 小畢姊說要加上訂單來源(orderSource)的條件 2020-03-20
             */
            String apiOrderSource = String.valueOf(tmpMap.get(ORDER_SOURCE));
            Date compareDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(apiPayDate);
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(compareDate);
            boolean check = false;
            for (Map<String, Object> map : resultList) {
                if (formattedDate.equals(map.get(PAY_DATE)) && map.get(GOODS_CODE).equals(apiGoodsCode)
                        && map.get(STRATEGY_CODE).equals(apiStrategyCode) && String.valueOf(map.get(ORDER_MODE)).equals(apiOrderMode)
                        && map.get(ORDER_SOURCE).equals(apiOrderSource) && map.get(PAY_METHOD).equals(apiPayMethod)) {
                    check = true;
                    Double payPrice = (Double) map.get(PAY_PRICE); //銷售金額
                    Double salesQty = (Double) map.get(QUANTITY);//銷售數量
                    int orderCount = (int) map.get(ORDER_COUNT);
                    payPrice += (Double) tmpMap.get(PAY_PRICE);
                    salesQty += (Double) tmpMap.get(QUANTITY);
                    orderCount++;
                    map.put(ORDER_COUNT, orderCount);
                    map.put(QUANTITY, salesQty);
                    map.put(PAY_PRICE, payPrice);
                    break;
                }
            }
            if (check == false) {
                tmpMap.put(PAY_DATE, formattedDate);
                tmpMap.put(ORDER_COUNT, 1);
                resultList.add(tmpMap);
            }
        }
//統計完才排除訂單單號，應該要統計前排除
//        resultList.removeIf(i -> orderNoList.contains(i.get("orderCode")));
        resultMap.put("list", resultList);
        resultMap.put("success", true);
        resultMap.put("message", "get success");

        return resultMap;
    }

    @Override
    public Object get(int pageNum, int pageSize, Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        DWQueryCondition condition = new DWQueryCondition();

        Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();

        if (!(boolean) permissionData.get("HaveData")) {
            result.put("message", "沒有配置數據權限資料");
        } else {

            StringBuilder sql = new StringBuilder();
            sql = sql.append(DBConstants.SELECT_CASE_FROM_SALES_STATISTICS);

            List<Object> sqlParams = new ArrayList<Object>();

            if (params.containsKey(CATEGORY_ID)) {
                List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
                condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
            }

            if (params.containsKey(GOODS_CODE)) {
                List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
                condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
            }

            if (params.containsKey(STRATEGY_CODE)) {
                List<String> strategyCodeList = (ArrayList<String>) params.get(STRATEGY_CODE);
                condition.addFieldInfo(STRATEGY_CODE, DWQueryValueOperator.In, strategyCodeList.toArray());
            }

            if (params.containsKey(ORDER_SOURCE)) {
                List<String> orderSourceList = (ArrayList<String>) params.get(ORDER_SOURCE);
                condition.addFieldInfo(ORDER_SOURCE, DWQueryValueOperator.In, orderSourceList.toArray());
            }

            String payMethod = (String) params.get(PAY_METHOD);
            if ("0".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
            } else if ("1".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
            }

            Object startDate = params.get(START_DATE);
            Object endDate = params.get(END_DATE);

            if (params.containsKey(START_DATE) && !params.containsKey(END_DATE)) {
                condition.addFieldInfo(PAY_DATE, DWQueryValueOperator.GreaterThanOrEqualTo, startDate);
            }

            if (params.containsKey(END_DATE) && !params.containsKey(START_DATE)) {
                condition.addFieldInfo(PAY_DATE, DWQueryValueOperator.LessThanOrEqualTo, endDate);
            }

            if (params.containsKey(START_DATE) && params.containsKey(END_DATE)) {
                condition.addBetweenInfo(PAY_DATE, startDate, endDate);
            }
            if (condition.getItems().size() > 0) {
                DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
                sql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
                sqlParams.addAll(conditionResult.getParametersAsList());
            }

            sql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
            sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key

            DWDataSetOperationOption option = new DWDataSetOperationOption();
            option.setManagementFieldEnabled(false);

            List<Map<String, Object>> totalData = dao.select(option, sql.toString(), sqlParams.toArray()); //總筆數
            int totalPageNum = (int) Math.ceil((float) totalData.size() / (float) pageSize); //總頁數

            sql.append(" " + DBConstants.STATISTICS_SQL_ORDER_BY_LIMIT); //分頁顯示
            int startIndex = pageSize * (pageNum - 1);
            sqlParams.add(startIndex);
            sqlParams.add(pageSize);
            List<Map<String, Object>> data = dao.select(option, sql.toString(), sqlParams.toArray());

            result.put("totalData", totalData.size());
            result.put("totalPageNum", totalPageNum);
            result.put("list", data);
            result.put("success", true);
            result.put("message", "get success");
        }
        return result;
    }

    @Override
    public Object putAssignedDate(String date) throws Exception {
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date updateDate = sdf.parse(date);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Date formatDate = sdf.parse(today);
        if (formatDate.equals(updateDate) || formatDate.before(updateDate)) {
            throw new DWArgumentException("date", updateDate + "更新日期不得大於等於今日!");
        } else {
            int updateStatus = 0;
            int successCount = 0;

            try {
                result = proxy.putSalesStatistics(date);
                updateStatus = (int) result.get("updateStatus");
                successCount = (int) result.get("successCount");

            } finally {

                Map<String, Object> profile = DWServiceContext.getContext().getProfile(); // 取得調用的"userId"以及"userName"
                String userId = (String) profile.get("userId");
                String userName = (String) profile.get("userName");

                Map<String, Object> historyMap = new HashMap<>();

                historyMap.put(UPDATE_DATE, date);
                historyMap.put(UPDATE_STATUS, updateStatus);
                historyMap.put(UPDATE_COUNT, successCount);
                historyMap.put(UPDATE_ID, userId);
                historyMap.put(UPDATE_NAME, userName);

                salesStatisticsHistory.post(historyMap);
            }
        }
        return result.get("message");
    }

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "bossTransactionManager", rollbackFor = Exception.class)
    public Map<String, Object> putSalesStatistics(String date) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PAY_DATE, date);

        int successCount = 0; //計算更新幾筆資料
        int updateStatus = 0; // 判斷更新成功與否

        Map<String, Object> resultMap = (Map<String, Object>) this.getAssignedDate(params);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        if (resultMap.containsKey("success")) {
            updateStatus = 1;
            List<Map<String, Object>> statisticsList = (List<Map<String, Object>>) resultMap.get("list");

            dao.update(option, DBConstants.DELETE_FROM_SALES_STATISTICS, date); //刪除原始資料

            String payDate = new String();   //PRIMARY KEY
            String goodsCode = new String();   //PRIMARY KEY
            String strategyCode = new String();   //PRIMARY KEY
            double orderMode2 = 3;   //PRIMARY KEY
            String payMethod = new String();   //PRIMARY KEY

            for (Map<String, Object> statisticsMap : statisticsList) {

                String orderSource = (String) statisticsMap.get(ORDER_SOURCE);
                if (statisticsMap.containsKey(PAY_DATE)) {
                    payDate = (String) statisticsMap.get(PAY_DATE); //PRIMARY KEY
                }
                String categoryId = (String) statisticsMap.get(CATEGORY_ID);
                if (statisticsMap.containsKey(GOODS_CODE)) {
                    goodsCode = (String) statisticsMap.get(GOODS_CODE); //PRIMARY KEY
                }
                String goodsName = (String) statisticsMap.get(GOODS_NAME);
                if (statisticsMap.containsKey(STRATEGY_CODE)) {
                    strategyCode = (String) statisticsMap.get(STRATEGY_CODE); //PRIMARY KEY
                }
                String strategyName = (String) statisticsMap.get(STRATEGY_NAME);
                if (statisticsMap.containsKey(ORDER_MODE)) {
                    orderMode2 = (double) statisticsMap.get(ORDER_MODE); //PRIMARY KEY
                }
                int orderMode = (int) orderMode2;

                int orderCount = (int) statisticsMap.get(ORDER_COUNT);

                double quantity2 = (double) statisticsMap.get(QUANTITY);
                int quantity = (int) quantity2;

                Double payPrice = (Double) statisticsMap.get(PAY_PRICE);

                if (statisticsMap.containsKey(PAY_METHOD)) {
                    payMethod = (String) statisticsMap.get(PAY_METHOD); //PRIMARY KEY
                }

                if (payDate != null && goodsCode != null && strategyCode != null && orderMode != 3) {
                    int insertResult = dao.update(option, DBConstants.INSERT_INTO_SALES_STATISTICS, orderSource, payDate, categoryId, goodsCode, goodsName,
                            strategyCode, strategyName, orderMode, orderCount, quantity, payPrice, payMethod);
                    successCount += insertResult;
                } else {
                    updateStatus = 0;
                    break;
                }
            }
        }
        Map<String, Object> StatisticsMap = new HashMap<>();
        StatisticsMap.put("updateStatus", updateStatus);
        StatisticsMap.put("successCount", successCount);
        StatisticsMap.put("message", "Success!  A total of " + successCount + " new data");

        if (updateStatus == 0) {
            throw new DWArgumentException("Failed", "Update Failed!");
        }
        return StatisticsMap;
    }

    @Override
    public Object getOverallTotal(Map<String, Object> params) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        DWQueryCondition condition = new DWQueryCondition();

        Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();

        StringBuilder sql = new StringBuilder();
        List<Object> sqlParams = new ArrayList<Object>();

        sql = sql.append(DBConstants.SELECT_FROM_SALES_STATISTICS_TOTAL);

        if (params.containsKey(CATEGORY_ID)) {
            List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
            if (categoryIdList.size() != 0) {
                condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
            }
        }

        if (params.containsKey(GOODS_CODE)) {
            List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
            if (goodsCodeList.size() != 0) {
                condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
            }
        }

        String payMethod = (String) params.get(PAY_METHOD);
        if ("0".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
        } else if ("1".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
        }

        if (condition.getItems().size() > 0) {

            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
            sql.append(" AND " + conditionResult.getSql());
            sqlParams.addAll(conditionResult.getParametersAsList());
        }

        sql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
        sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        result = dao.select(option, sql.toString(), sqlParams.toArray());

        Map<String, Object> map = new HashMap<>();
        map.put("totalOrderCount", 0);
        map.put("totalPayPrice", 0);
        map.put("averageUnitPayPrice", 0);
        if (result.size() == 0) {
            result.add(map);
        } else {
            Map<String, Object> resultMap = result.get(0);
            if (resultMap.get("totalOrderCount") == null || resultMap.get("totalPayPrice") == null || resultMap.get("averageUnitPayPrice") == null) {
                resultMap.put("totalOrderCount", 0);
                resultMap.put("totalPayPrice", 0);
                resultMap.put("averageUnitPayPrice", 0);
            }
        }

        if (!(boolean) permissionData.get("HaveData")) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", result.get(0));
        }
        return DWServiceResultBuilder.build(result.get(0));

    }

    @Override
    public Object getMonthTotalDetails(String monthOfYear, Map<String, Object> params) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> newData = new ArrayList<>();
        DWQueryCondition condition = new DWQueryCondition();

        Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();

        if (monthOfYear.length() == 7 && monthOfYear.contains("-")) {

            StringBuilder sql = new StringBuilder();
            List<Object> sqlParams = new ArrayList<Object>();

            sql = sql.append(DBConstants.SELECT_FROM_SALES_STATISTICS_MONTHTOTAL);
            sqlParams.add(monthOfYear + "%");

            if (params.containsKey(CATEGORY_ID)) {
                List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
                if (categoryIdList.size() != 0) {
                    condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
                }
            }

            if (params.containsKey(GOODS_CODE)) {
                List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
                if (goodsCodeList.size() != 0) {
                    condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
                }
            }

            String payMethod = (String) params.get(PAY_METHOD);
            if ("0".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
            } else if ("1".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
            }

            if (condition.getItems().size() > 0) {
                DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
                sql.append(" AND " + conditionResult.getSql());
                sqlParams.addAll(conditionResult.getParametersAsList());
            }

            sql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
            sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key

            sql.append(DBConstants.SALES_STATISTICS_GROUP_BY_DAY_PAY_DATE);

            DWDataSetOperationOption option = new DWDataSetOperationOption();
            option.setManagementFieldEnabled(false);

            result = dao.select(option, sql.toString(), sqlParams.toArray());
            String[] ym = monthOfYear.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(ym[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(ym[1]) - 1);
            int dayCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            Set<Integer> dayList = result.stream().map(item -> (int) item.get(PAY_DATE)).collect(Collectors.toSet());

            for (int i = 1; i <= dayCount; i++) {
                Map<String, Object> dayData = new HashMap<>();
                if (!dayList.contains(i)) {
                    dayData.put(PAY_DATE, i);
                    dayData.put(TOTAL_PAY_PRICE, 0);
                    dayData.put(AVERAGE_UNIT_PAY_PRICE, 0);
                    dayData.put(TOTAL_QUANTITY, 0);
                    dayData.put(TOTAL_ORDER_COUNT, 0);
                    newData.add(dayData);
                }
            }
            result.addAll(newData);

            Collections.sort(result, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer day1 = (Integer) o1.get(PAY_DATE);
                    Integer day2 = (Integer) o2.get(PAY_DATE);
                    return day1.compareTo(day2);
                }
            });

            result.forEach(item -> item.remove(PAY_DATE));

        } else {
            return DWServiceResultBuilder.build("請輸入正確的年月格式 -> yyyy-MM", result);
        }
        if (!(boolean) permissionData.get("HaveData")) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", result);
        }
        return DWServiceResultBuilder.build(result);

    }

    @Override
    public Object getYearTotalDetails(String year, Map<String, Object> params) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> newData = new ArrayList<>();
        DWQueryCondition condition = new DWQueryCondition();

        Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();

        if (year.length() == 4) {

            StringBuilder sql = new StringBuilder();
            List<Object> sqlParams = new ArrayList<Object>();

            sql = sql.append(DBConstants.SELECT_FROM_SALES_STATISTICS_YEARTOTAL);
            sqlParams.add(year + "%");

            if (params.containsKey(CATEGORY_ID)) {
                List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
                if (categoryIdList.size() != 0) {
                    condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
                }
            }

            if (params.containsKey(GOODS_CODE)) {
                List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
                if (goodsCodeList.size() != 0) {
                    condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
                }
            }

            String payMethod = (String) params.get(PAY_METHOD);
            if ("0".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
            } else if ("1".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
            }

            if (condition.getItems().size() > 0) {
                DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
                sql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
                sqlParams.addAll(conditionResult.getParametersAsList());
            }

            sql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
            sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key

            sql.append(DBConstants.SALES_STATISTICS_GROUP_BY_MONTH_PAY_DATE);

            DWDataSetOperationOption option = new DWDataSetOperationOption();
            option.setManagementFieldEnabled(false);

            result = dao.select(option, sql.toString(), sqlParams.toArray());

            Set<Integer> dayList = result.stream().map(item -> (int) item.get(PAY_DATE)).collect(Collectors.toSet());

            for (int i = 1; i <= 12; i++) {
                Map<String, Object> dayData = new HashMap<>();
                if (!dayList.contains(i)) {
                    dayData.put(PAY_DATE, i);
                    dayData.put(TOTAL_PAY_PRICE, 0);
                    dayData.put(AVERAGE_UNIT_PAY_PRICE, 0);
                    dayData.put(TOTAL_QUANTITY, 0);
                    dayData.put(TOTAL_ORDER_COUNT, 0);
                    newData.add(dayData);
                }
            }
            result.addAll(newData);

            Collections.sort(result, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer day1 = (Integer) o1.get(PAY_DATE);//name1是从你list里面拿出来的一个
                    Integer day2 = (Integer) o2.get(PAY_DATE); //name1是从你list里String面拿出来的第二个name
                    return day1.compareTo(day2);
                }
            });

            result.forEach(item -> item.remove(PAY_DATE));

        } else {
            return DWServiceResultBuilder.build("請輸入正確的年格式 -> yyyy", result);
        }

        if (!(boolean) permissionData.get("HaveData")) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", result);
        }
        return DWServiceResultBuilder.build(result);
    }

    //排名
    public Object getSalesRank(String field, Map<String, Object> params) throws Exception {

        DWQueryCondition condition = new DWQueryCondition();
        DWQueryCondition totalCondition = new DWQueryCondition();

        List<Map<String, Object>> data = new ArrayList();

        Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();

        if (!(boolean) permissionData.get("HaveData")) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", data);
        } else {

            StringBuilder sql = new StringBuilder();
            List<Object> sqlParams = new ArrayList<Object>();

            String totalFiledSql = new String();
            List<Object> totalSqlParams = new ArrayList<Object>();

            sql = sql.append(DBConstants.SELECT_FROM_SALES_STATISTICS_GROUP_BY_FIELD);

            if (params.containsKey(CATEGORY_ID)) {
                List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
                if (categoryIdList.size() != 0) {
                    condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
                }
            }

            if (params.containsKey(GOODS_CODE)) {
                List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
                if (goodsCodeList.size() != 0) {
                    condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
                }
            }

            String payMethod = (String) params.get(PAY_METHOD);
            if ("0".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
            } else if ("1".equals(payMethod)) {
                condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
            }

            Object startDate = params.get(START_DATE);
            Object endDate = params.get(END_DATE);

            if (params.containsKey(START_DATE) && !params.containsKey(END_DATE)) {
                condition.addFieldInfo(PAY_DATE, DWQueryValueOperator.GreaterThanOrEqualTo, startDate);
                totalCondition.addFieldInfo(PAY_DATE, DWQueryValueOperator.GreaterThanOrEqualTo, startDate);
            }

            if (params.containsKey(END_DATE) && !params.containsKey(START_DATE)) {
                condition.addFieldInfo(PAY_DATE, DWQueryValueOperator.LessThanOrEqualTo, endDate);
                totalCondition.addFieldInfo(PAY_DATE, DWQueryValueOperator.LessThanOrEqualTo, endDate);
            }

            if (params.containsKey(START_DATE) && params.containsKey(END_DATE)) {
                condition.addBetweenInfo(PAY_DATE, startDate, endDate);
                totalCondition.addBetweenInfo(PAY_DATE, startDate, endDate);
            }

            int rank = 9999999;
            if (params.containsKey("rank")) {
                rank = Integer.valueOf((String) params.get("rank"));
            }

            String permissionSql = " " + permissionData.get("sql").toString().replaceAll("d1.", "");
            totalFiledSql = String.format(DBConstants.SELECT_FROM_SALES_STATISTICS_TOTAL_FILED, field) + permissionSql;
            totalSqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE));

            if (condition.getItems().size() > 0) {
                DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
                sql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
                sqlParams.addAll(conditionResult.getParametersAsList());
            }
            if (totalCondition.getItems().size() > 0) {
                DWSqlInfo totalConditionResult = ((DWDaoImpl) dao).getDialect().parse(totalCondition);
                totalFiledSql = totalFiledSql + " AND " + totalConditionResult.getSql(); //入參params組成的SQL
                totalSqlParams.addAll(totalConditionResult.getParametersAsList());
            }
            sql.append(permissionSql); //數據權限過濾組成的SQL(變數存取)
            sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key
            sql = sql.append(String.format(DBConstants.SELECT_FROM_SALES_STATISTICS_GROUP_BY, field, rank));

            DWDataSetOperationOption option = new DWDataSetOperationOption();
            option.setManagementFieldEnabled(false);

            data = dao.select(option, sql.toString(), sqlParams.toArray());

            List<Map<String, Object>> totalFiled = dao.select(option, totalFiledSql, totalSqlParams.toArray());

            if (totalFiled.get(0).get("total") == null) {
                totalFiled.get(0).put("total", 0);
            }

            Double total = Double.valueOf(totalFiled.get(0).get("total").toString());

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(4);

            for (int i = 0; i < data.size(); i++) {
                Double percent = Double.valueOf(nf.format((Double.valueOf(data.get(i).get(field).toString()) / total) * 100).toString());
                data.get(i).put("percentage", percent);
            }
        }
        return DWServiceResultBuilder.build(data);
    }

    @Override
    public Object getTodayTotal(Map<String, Object> params) throws Exception {
        Map<String, Object> map = this.getDefaultResultMap();
        Map<String, Object> permissionData;

        try {
            permissionData = this.getPermissionData();
        } catch (IllegalAccessException e) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", map);
        } catch (Exception e) {
            throw e;
        }

        List<String> categoryIdList = new ArrayList<>();
        if (params.containsKey(CATEGORY_ID)) {
            categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
        }

        List<String> goodsCodeList = new ArrayList<>();
        if (params.containsKey(GOODS_CODE)) {
            goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
        }

        String payMethod = (String) params.get(PAY_METHOD);

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Map<String, Object> listMap = (Map<String, Object>) this.getAssignedDate(new HashMap<String, Object>() {{
            this.put("payDate", today);
        }});

        List<Map<String, Object>> list = (List<Map<String, Object>>) listMap.get("list");
        DWUserPermission userPermission = (DWUserPermission) permissionData.get("userPermission");
        DWRowPermissionIncludedMatchOption option = new DWRowPermissionIncludedMatchOption();
        List<Map<String, Object>> userViews = userPermission.getRowPermission().filter(list, option);

        int orderCount = 0;
        double totalPayPrice = 0;

        for (Map<String, Object> permissionMap : userViews) {
            double orderMode = (double) permissionMap.get("orderMode");
            if ((goodsCodeList.size() == 0 || goodsCodeList.contains(permissionMap.get(GOODS_CODE))) &&
                    (categoryIdList.size() == 0 || categoryIdList.contains(permissionMap.get(CATEGORY_ID))) &&
                    orderMode == 1.0 && (payMethod == null || payMethod.isEmpty() || (("0".equals(payMethod) &&
                    permissionMap.get(PAY_METHOD).equals(OFFLINE)) || ("1".equals(payMethod) && !permissionMap.get(PAY_METHOD).equals(OFFLINE))))) {

                totalPayPrice += (Double) permissionMap.get(PAY_PRICE);
                orderCount += (Integer) permissionMap.get(ORDER_COUNT);
            }
        }

        double avgUnitPrice = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        if (totalPayPrice != 0 && orderCount != 0) {
            avgUnitPrice = Double.parseDouble(decimalFormat.format(totalPayPrice / orderCount));
        }

        map.put("averageUnitPayPrice", avgUnitPrice);
        map.put("totalPayPrice", totalPayPrice);
        map.put("totalOrderCount", orderCount);

        return DWServiceResultBuilder.build("success", map);
    }

    /**
     * 取得指定月份銷售統計數據總計服務
     *
     * @param monthOfYear 必填，格式：2020-02 7碼
     * @param params      ex:{"categoryId":"service","goodsCode":"dcx2"} categoryId/goodsCode選填 (可多選)
     * @return 當月銷售金額、平均單價(取到小數後2位)、交易筆數；當月銷售金額/交易筆數=平均單價
     * @throws Exception
     */
    @Override
    public Object getMonthTotal(String monthOfYear, Map<String, Object> params) throws Exception {
        if (monthOfYear == null || monthOfYear.isEmpty()) {
            throw new DWArgumentException("monthOfYear", "monthOfYear is null or empty!");
        }

        try {
            monthOfYear = this.translateToCorrectDateString("year-month", monthOfYear);
        } catch (Exception e) {
            throw new DWArgumentException("monthOfYear", "monthOfYear日期格式錯誤，正確格式為yyyy-MM");
        }

        Map<String, Object> map = this.getDefaultResultMap();
        Map<String, Object> permissionData;

        try {
            permissionData = this.getPermissionData();
        } catch (IllegalAccessException e) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", map);
        } catch (Exception e) {
            throw e;
        }

        DWQueryCondition condition = new DWQueryCondition();
        StringBuilder resultSql = new StringBuilder();
        resultSql = resultSql.append(COUNT_TOTAL_SQL);
        List<Object> sqlParams = new ArrayList<>();
        sqlParams.add(monthOfYear + "%"); //取到年月進行模糊查詢

        if (params.containsKey(CATEGORY_ID)) {
            List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
            if (categoryIdList.size() != 0) {
                condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
            }
        }

        if (params.containsKey(GOODS_CODE)) {
            List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
            if (goodsCodeList.size() != 0) {
                condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
            }
        }

        String payMethod = (String) params.get(PAY_METHOD);
        if ("0".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
        } else if ("1".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
        }

        if (condition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
            resultSql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
            sqlParams.addAll(conditionResult.getParametersAsList());
        }

        resultSql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
        sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> result = this.dao.select(option, resultSql.toString(), sqlParams.toArray());

        if (result.size() == 0) {
            // 如果直接操作 result 會拋出 java.lang.UnsupportedOperationException
            result = new ArrayList<Map<String, Object>>() {{
                this.add(map);
            }};
        } else {
            Map<String, Object> resultMap = result.get(0);
            if (
                    resultMap.get("totalOrderCount") == null ||
                            resultMap.get("totalPayPrice") == null ||
                            resultMap.get("averageUnitPayPrice") == null
            ) {
                result.get(0).put("totalOrderCount", 0);
                result.get(0).put("totalPayPrice", 0);
                result.get(0).put("averageUnitPayPrice", 0);
            }
        }

        return DWServiceResultBuilder.build(result.get(0));
    }

    /**
     * 取得指定年份銷售統計數據總計服務
     *
     * @param year   僅傳入年份
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public Object getYearTotal(String year, Map<String, Object> params) throws Exception {
        if (year == null || year.isEmpty()) {
            throw new DWArgumentException("year", "year is null or empty!");
        }

        try {
            year = this.translateToCorrectDateString("year", year);
        } catch (Exception e) {
            throw new DWArgumentException("year", "year日期格式錯誤，正確格式為yyyy");
        }

        Map<String, Object> map = this.getDefaultResultMap();
        Map<String, Object> permissionData;

        try {
            permissionData = this.getPermissionData();
        } catch (IllegalAccessException e) {
            return DWServiceResultBuilder.build("沒有配置數據權限資料", map);
        } catch (Exception e) {
            throw e;
        }

        DWQueryCondition condition = new DWQueryCondition();
        StringBuilder resultSql = new StringBuilder();
        resultSql = resultSql.append(COUNT_TOTAL_SQL);
        List<Object> sqlParams = new ArrayList<>();
        sqlParams.add(year + "%"); //取到年月進行模糊查詢

        if (params.containsKey(CATEGORY_ID)) {
            List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
            if (categoryIdList.size() != 0) {
                condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
            }
        }

        if (params.containsKey(GOODS_CODE)) {
            List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
            if (goodsCodeList.size() != 0) {
                condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
            }
        }

        String payMethod = (String) params.get(PAY_METHOD);
        if ("0".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
        } else if ("1".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
        }

        if (condition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
            resultSql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
            sqlParams.addAll(conditionResult.getParametersAsList());
        }

        resultSql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
        sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> result = this.dao.select(option, resultSql.toString(), sqlParams.toArray());

        if (result.size() == 0) {
            // 如果直接操作 result 會拋出 java.lang.UnsupportedOperationException
            result = new ArrayList<Map<String, Object>>() {{
                this.add(map);
            }};
        } else {
            Map<String, Object> resultMap = result.get(0);
            if (
                    resultMap.get("totalOrderCount") == null ||
                            resultMap.get("totalPayPrice") == null ||
                            resultMap.get("averageUnitPayPrice") == null
            ) {
                result.get(0).put("totalOrderCount", 0);
                result.get(0).put("totalPayPrice", 0);
                result.get(0).put("averageUnitPayPrice", 0);
            }
        }

        return DWServiceResultBuilder.build(result.get(0));
    }

    /**
     * 取得前七日銷售統計數據總計明細服務
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public Object getPreviousSevenDaysTotalDetails(Map<String, Object> params) throws Exception {
        Date date = this.getToday();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar firstCal = Calendar.getInstance();
        firstCal.setTime(date);
        firstCal.add(Calendar.DATE, -7);
        Date firstDate = firstCal.getTime();
        String firstDay = dateFormat.format(firstDate);

        List<String> sevenDayList = new ArrayList<>();
        while (firstCal.getTime().before(date)) {
            sevenDayList.add(dateFormat.format(firstCal.getTime()));
            firstCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Map<String, Object> permissionData;

        try {
            permissionData = this.getPermissionData();
        } catch (IllegalAccessException e) {
            List<Map<String, Object>> result = new ArrayList<>();

            for (String payDate : sevenDayList) {
                Map<String, Object> map = this.getDefaultResultMapForMoreInfo();
                map.put("payDate", dateFormat.parse(payDate));
                result.add(map);
            }

            return DWServiceResultBuilder.build("沒有配置數據權限資料", result);
        } catch (Exception e) {
            throw e;
        }

        Calendar seventhCal = Calendar.getInstance();
        seventhCal.setTime(date);
        seventhCal.add(Calendar.DATE, -1);
        Date seventhDate = seventhCal.getTime();
        String seventhDay = dateFormat.format(seventhDate);
        DWQueryCondition condition = new DWQueryCondition();
        StringBuilder resultSql = new StringBuilder();
        resultSql = resultSql.append(SHOW_YEAR_INFO_SQL);
        List<Object> sqlParams = new ArrayList<Object>() {{
            this.add(firstDay);
            this.add(seventhDay);
        }};

        if (params.containsKey(CATEGORY_ID)) {
            List<String> categoryIdList = (ArrayList<String>) params.get(CATEGORY_ID);
            if (categoryIdList.size() != 0) {
                condition.addFieldInfo(CATEGORY_ID, DWQueryValueOperator.In, categoryIdList.toArray());
            }
        }

        if (params.containsKey(GOODS_CODE)) {
            List<String> goodsCodeList = (ArrayList<String>) params.get(GOODS_CODE);
            if (goodsCodeList.size() != 0) {
                condition.addFieldInfo(GOODS_CODE, DWQueryValueOperator.In, goodsCodeList.toArray());
            }
        }

        String payMethod = (String) params.get(PAY_METHOD);
        if ("0".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.Equals, OFFLINE);
        } else if ("1".equals(payMethod)) {
            condition.addFieldInfo(PAY_METHOD, DWQueryValueOperator.NotEquals, OFFLINE);
        }

        if (condition.getItems().size() > 0) {
            DWSqlInfo conditionResult = ((DWDaoImpl) dao).getDialect().parse(condition);
            resultSql.append(" AND " + conditionResult.getSql()); //入參params組成的SQL
            sqlParams.addAll(conditionResult.getParametersAsList());
        }

        resultSql.append(" " + permissionData.get("sql").toString().replaceAll("d1.", "")); //數據權限過濾組成的SQL
        sqlParams.addAll((Collection<?>) permissionData.get(GOODS_CODE)); //這邊GOODS_CODE為Map Key
        resultSql.append("GROUP BY payDate ");

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> result = new ArrayList<>(this.dao.select(option, resultSql.toString(), sqlParams.toArray()));

        //抓出payDate
        List<String> dateList = new ArrayList<>();
        for (Map<String, Object> item : result) {
            dateList.add(dateFormat.format(item.get("payDate")));
        }

        for (String payDate : sevenDayList) {
            if (dateList.contains(payDate)) {
                continue;
            }

            Map<String, Object> map = this.getDefaultResultMapForMoreInfo();
            map.put("payDate", dateFormat.parse(payDate));
            result.add(map);
        }

        Collections.sort(result, (o1, o2) -> {
            Date date1 = (Date) o1.get("payDate");
            Date date2 = (Date) o2.get("payDate");

            return date1.compareTo(date2);
        });

        return DWServiceResultBuilder.build("success", result);
    }


    private String translateToCorrectDateString(String type, String value) throws IllegalArgumentException, ParseException {
        String result = null;

        try {
            SimpleDateFormat format;

            switch (type.toLowerCase()) {
                case "year":
                    format = new SimpleDateFormat("yyyy");
                    break;

                case "year-month":
                    format = new SimpleDateFormat("yyyy-MM");
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            format.setLenient(false);

            result = format.format(format.parse(value));
        } catch (ParseException | IllegalArgumentException e) {
            throw e;
        }

        return result;
    }

    private Date getToday() {
        return new Date();
    }

    private Map<String, Object> getDefaultResultMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalOrderCount", 0);
        map.put("totalPayPrice", 0);
        map.put("averageUnitPayPrice", 0);
        return map;
    }

    private Map<String, Object> getDefaultResultMapForMoreInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("payDate", "");
        map.put("totalQuantity", 0);
        map.put("totalOrderCount", 0);
        map.put("totalPayPrice", 0);
        map.put("averageUnitPayPrice", 0);
        return map;
    }

    private Map<String, Object> getPermissionData() throws Exception, IllegalAccessException {
        Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();
        if (!(boolean) permissionData.getOrDefault("HaveData", false)) {
            throw new IllegalAccessException();
        }
        return permissionData;
    }
}
