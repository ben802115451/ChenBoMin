package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.boss.dwreport.dao.DBConstants;
import com.digiwin.boss.dwreport.service.IExcludeOrderService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Miko
 */
public class ExcludeOrderService implements IExcludeOrderService {
    private static final Log log = LogFactory.getLog(ExcludeOrderService.class);

    @Autowired
    SalesStatisticsService salesStatisticsService;

    @Autowired
    @Qualifier("bossDao")
    private DWDao dao;

    /**
     * 新增欲排除的訂單
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    @Override
    public Object post(DWDataSet dataset) throws Exception {
        DWDataTable table = dataset.getTable(DBConstants.EXCLUDE_ORDER);
        List<String> orderCodeList = new ArrayList<>();

        DWQueryInfo queryInfo = new DWQueryInfo();
        queryInfo.setTableName(DBConstants.EXCLUDE_ORDER);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        for (DWDataRow row : table.getRows()) {
            String orderCode = (String) row.get(DBConstants.ORDER_CODE);
            if (orderCode == null || orderCode.isEmpty()) {
                throw new DWArgumentException("orderCode", "orderCode is null or empty!");
            }
            queryInfo.addEqualInfo(DBConstants.ORDER_CODE, orderCode);
            DWDataSet data = this.dao.select(queryInfo, option);

            for (DWDataRow dataRow : data.getTable(DBConstants.EXCLUDE_ORDER).getRows()) {
                String dbOrderCode = (String) dataRow.get(DBConstants.ORDER_CODE);
                if (dbOrderCode != null || !dbOrderCode.isEmpty()) {
                    throw new DWBusinessException("\"" + dbOrderCode + "\"" + "訂單編號已存在, 請重新輸入");
                }
            }
            orderCodeList.add(orderCode);
        }
        Set<String> date = this.process(orderCodeList);

        Object result = this.dao.execute(dataset, option);

        this.updateProcess(date);

        return DWServiceResultBuilder.build("新增成功", result);
    }

    /**
     * 修改欲排除的訂單
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    @Override
    public Object put(DWDataSet dataset) throws Exception {
        DWDataTable table = dataset.getTable(DBConstants.EXCLUDE_ORDER);
        for (DWDataRow row : table.getRows()) {
            String orderCode = (String) row.get(DBConstants.ORDER_CODE);
            if (orderCode == null || orderCode.isEmpty()) {
                throw new DWArgumentException("orderCode", "orderCode is null or empty!");
            }
        }
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.execute(dataset, option);
        return DWServiceResultBuilder.build("修改成功", result);
    }

    /**
     * 刪除欲排除的訂單
     * 刪除該筆資料後 也要自動執行[更新數據]
     *
     * @param dataset
     * @return
     * @throws Exception
     */
    @Override
    public Object delete(DWDataSet dataset) throws Exception {
        DWDataTable table = dataset.getTable(DBConstants.EXCLUDE_ORDER);
        List<String> orderCodeList = new ArrayList<>();
        Iterator<DWDataRow> removedRows = table.getRows().getIteratorOfRemovedRows();
        while (removedRows.hasNext()) {
            String orderCode = removedRows.next().get(DBConstants.ORDER_CODE).toString();
            orderCodeList.add(orderCode);
        }

        Set<String> date = this.process(orderCodeList);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        Object result = this.dao.execute(dataset, option);

        this.updateProcess(date);

        return DWServiceResultBuilder.build("刪除成功", result);
    }

    /**
     * 取得欲排除的訂單
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object get() throws Exception {
        DWQueryInfo queryInfo = new DWQueryInfo();
        queryInfo.setTableName(DBConstants.EXCLUDE_ORDER);

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        DWDataSet dataset = this.dao.select(queryInfo, option);
        return DWServiceResultBuilder.build(dataset);
    }

    public Set<String> process(List<String> orderCodeList) throws Exception {
        //呼叫/api/omc/v2/orders/code/{orderCode}，傳入訂單單號去取payDate(payment/createTime即為payDate)
        String omcUrl = DWModuleConfigUtils.getCurrentModuleProperty("omcUrl");
        String orderCodeApi = DWModuleConfigUtils.getCurrentModuleProperty("orderCodeApi");
        // header
        String userToken = DWServiceContext.getContext().getToken();
        Set<String> date = new HashSet<>();

        for (String orderCode : orderCodeList) {
            String orderCodeUrl = String.format("%s%s?orderCode=%s", omcUrl, orderCodeApi, orderCode);
            log.info(">>>get orderCodeUrl url = " + orderCodeUrl);

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(orderCodeUrl);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("digi-middleware-auth-user", userToken);
            HttpResponse response = client.execute(request);

            String content = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            Map<String, Object> map = DWGsonProvider.getGson().fromJson(content, Map.class);

            if (statusCode != HttpStatus.SC_OK) {

                StringBuilder errorMessage = new StringBuilder();
//                errorMessage.append("Get orderCodeApi failed, status code = ").append(statusCode)
//                        .append(",").append(map.get("message"));
                errorMessage.append(map.get("message"));

                log.error("IExcludeOrderService.process failed! -> " + content);

                throw new DWException(errorMessage.toString());
            }
//            String apiStatusCode = (String) map.get("code");
//            if (apiStatusCode.equals("200")) {
            Map<String, Object> orderMap = (Map<String, Object>) map.get("order");
            Map<String, Object> orderStatusMap = (Map<String, Object>) orderMap.get("orderState");
            String orderStatus = orderStatusMap.get("orderStatus").toString();
            if (orderStatus.equals("Paid")) {
                Map<String, Object> paymentMap = (Map<String, Object>) orderMap.get("payment");
                String payDate = paymentMap.get("createTime").toString();
                Date compareDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(payDate);
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(compareDate);
                String compareDate2 = formattedDate.replace("-", "");
                String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
                //若維護的為今日的訂單則不呼叫putAssignedDate，因為每日00：00會更新

                if (Integer.parseInt(compareDate2) < Integer.parseInt(today)) {
                    date.add(formattedDate);
                }
            } else {
                throw new DWArgumentException("orderCode", orderCode + " 該張訂單未付款，請輸入已付款的訂單編號！");
            }
//            } else {
//                throw new DWArgumentException("orderCode", orderCode + " not exist!");
//            }
        }
        return date;
    }

    public void updateProcess(Set<String> date) throws Exception {
        for (String updateDate : date) salesStatisticsService.putAssignedDate(updateDate);
    }
}
