package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.boss.dwreport.service.IOrderService;
import com.digiwin.boss.dwreport.service.utils.DWExcelBuilder;
import com.digiwin.boss.dwreport.service.utils.DWExcelCellValueBaseConverter;
import com.digiwin.boss.dwreport.service.utils.DWExcelColumnSetting;
import com.digiwin.boss.dwreport.service.utils.DWExcelSheetSetting;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.MAX_VALUE;

/**
 * @author Miko
 */
public class OrderService implements IOrderService {
    private static final Log log = LogFactory.getLog(OrderService.class);

    @Override
    public Object getList(int pageNum, int pageSize, Map<String, Object> params) throws Exception {
        String module = DWServiceContext.getContext().getModuleName();
        String omcUrl = DWModuleConfigUtils.getCurrentModuleProperty("omcUrl"); // 記得調整成getCurrent....
        String orderListApi = DWModuleConfigUtils.getProperty(module, "orderListApi");

        // header
        DWServiceContext context = DWServiceContext.getContext();
        Map<String, Object> requestHeader = context.getRequestHeader();
        String userToken = (String) requestHeader.get("token");

        Gson gson = new Gson();
        String paramJson = gson.toJson(params);
        String encodedParams = URLEncoder.encode(paramJson, "UTF-8"); // 棄用了要記得調整

        /**
         * 調用OMC api 192.168.9.27:22614/api/omc/v2/orders/list pageNum： pageSize：設定顯示筆數
         * params：
         */
        String orderListUrl = String.format("%s%s?pageNum=%s&pageSize=%s&params=%s", omcUrl, orderListApi, pageNum,
                pageSize, encodedParams);
        log.info(">>>get orderList url = " + orderListUrl);
        JSONObject json = new JSONObject(params);
        StringEntity orderListEntity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        HttpClient orderListClient = HttpClientBuilder.create().build();
        HttpPost orderRequest = new HttpPost(orderListUrl);
        orderRequest.setHeader("Content-Type", "application/json");
        orderRequest.setHeader("digi-middleware-auth-user", userToken);
        orderRequest.setEntity(orderListEntity);
        HttpResponse orderResponse = orderListClient.execute(orderRequest);
        String orderContent = EntityUtils.toString(orderResponse.getEntity());
        int orderStatusCode = orderResponse.getStatusLine().getStatusCode();
        if (orderStatusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get OrderService List failed, status code = ").append(orderStatusCode)
                    .append(", please check the log for more information.");

            log.error("IOrderService.getOrderList failed! -> " + orderContent);

            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> resultMap = DWGsonProvider.getGson().fromJson(orderContent, Map.class);
        resultMap.put("success", true);
        resultMap.put("message", "get success");

        return resultMap;
    }

    @Override
    public Object getExcel(Map<String, Object> params) throws Exception {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String fileName = "OrderService.xlsx";
        DWServiceContext context = DWServiceContext.getContext();
        Map<String, Object> responseHeader = context.getResponseHeader();
        responseHeader.put("Content-Type", contentType);
        responseHeader.put("Content-disposition", "attachment;filename=" + fileName);

        Map<String, Object> map = (Map<String, Object>) this.getList(1, (int) MAX_VALUE, params);
//        Map<String, Object> map = (Map<String, Object>) this.getList(1, 1000, params);
        List<Map<String, Object>> datas = (List<Map<String, Object>>) map.get("list");

        DWExcelBuilder excelBuilder = new DWExcelBuilder();
        DWExcelSheetSetting sheetSetting = excelBuilder.addSheet(DWModuleMessageResourceBundleUtils.getString("sheetName"));
        sheetSetting.addColumnByModuleI18nKey("tenantName").setDataMappingKeys(new String[]{"tenantName", "tenantId"}, "%s(%s)");
        sheetSetting.addColumnByModuleI18nKey("goodsName").setDataMappingKeys(new String[]{"goodsName", "goodsCode"}, "%s(%s)");
        sheetSetting.addColumnByModuleI18nKey("strategyName").setDataMappingKey("strategyName");
        /**
         * z5969 條件加"性質"(全部\一般訂單\購物車訂單)  預設一般訂單，導出excel也要添加性質這欄位 2020-09-10 Miko
         *   0.一般订单，1.购物订单，2.组合订单
         */
        sheetSetting.addColumnByModuleI18nKey("property").setDataMappingKey("shopping").setIntegerType()
                .addDataConvertion(0, DWModuleMessageResourceBundleUtils.getString("general"))
                .addDataConvertion(1, DWModuleMessageResourceBundleUtils.getString("shoppingCart"))
                .addDataConvertion(2, DWModuleMessageResourceBundleUtils.getString("associationOrder"));

        DWExcelColumnSetting orderCodeSetting = sheetSetting.addColumnByModuleI18nKey("orderCode");
        orderCodeSetting.getCellProcessor().setCellValueConverter(new orderCodeValueConversion());
        orderCodeSetting.setDataMappingKeys(new String[]{"shopping", "orderCode", "packCode"}, "%s%s%s");

        /**
         * 新增cartCode購物單編號 2020-02-26 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("cartCode").setDataMappingKey("cartCode");

        DWExcelColumnSetting packCodeSetting = sheetSetting.addColumnByModuleI18nKey("associationOrderNo");
        packCodeSetting.getCellProcessor().setCellValueConverter(new packCodeValueConversion());
        packCodeSetting.setDataMappingKeys(new String[]{"shopping", "packCode"}, "%s%s");



        /**
         * z1826 匯出excel也要添加組合訂單號、組合商品ID、組合商品名稱 2020-09-17 Miko
         * packCode(組合訂單號)、packGoodsCode(組合商品ID)、packGoodsName(組合商品名稱)
         */
//        sheetSetting.addColumnByModuleI18nKey("associationOrderNo").setDataMappingKey("packCode");
        sheetSetting.addColumnByModuleI18nKey("associationGoodsName").setDataMappingKeys(new String[]{"packGoodsName", "packGoodsCode"}, "%s(%s)");
        sheetSetting.addColumnByModuleI18nKey("orderDate").setDataMappingKey("orderDate");
        /**
         * 新增shipmentStartDate、shipmentEndDate、authorizationDate三個欄位 2020-02-10 Miko
         * Z732 6. 訂單明細表新增 查詢條件及列表新增:出貨起訖日 預計開通日
         * 出貨起訖日拆成兩個欄位顯示 2020-02-13 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("shipmentStartDate").setDataMappingKey("shipmentStartDate");
        sheetSetting.addColumnByModuleI18nKey("shipmentEndDate").setDataMappingKey("shipmentEndDate");
        sheetSetting.addColumnByModuleI18nKey("authorizationDate").setDataMappingKey("authorizationDate");
        /**
         * 订单状态（0.未付款 1.已付款 2.已退款 3.待付款） 2019/12/11 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("orderStatus").setDataMappingKey("orderStatus").setIntegerType()
                .addDataConvertion(0, DWModuleMessageResourceBundleUtils.getString("unpaid"))
                .addDataConvertion(1, DWModuleMessageResourceBundleUtils.getString("paid"))
                .addDataConvertion(2, DWModuleMessageResourceBundleUtils.getString("refunded"))
                .addDataConvertion(3, DWModuleMessageResourceBundleUtils.getString("outstandingPayment"));
        sheetSetting.addColumnByModuleI18nKey("quantity").setDataMappingKey("quantity").setIntegerType();
        sheetSetting.addColumnByModuleI18nKey("totalAmount").setDataMappingKey("totalPrice");
        sheetSetting.addColumnByModuleI18nKey("taxExcludedPayment").setDataMappingKey("noTaxPrice");
        sheetSetting.addColumnByModuleI18nKey("payment").setDataMappingKey("payPrice");
        /**
         * Z2298 企業類型---> 0-个人租户，1-企业租户，2-个人开发商，3-企业开发商 2020-12-23 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("tenantType").setDataMappingKey("enterprise").setIntegerType()
                .addDataConvertion(0, DWModuleMessageResourceBundleUtils.getString("personal"))
                .addDataConvertion(1, DWModuleMessageResourceBundleUtils.getString("business"))
                .addDataConvertion(2, DWModuleMessageResourceBundleUtils.getString("personalDeveloper"))
                .addDataConvertion(3, DWModuleMessageResourceBundleUtils.getString("businessDeveloper"));
        sheetSetting.addColumnByModuleI18nKey("subscriptionAccount").setDataMappingKeys(new String[]{"userName", "userId"}, "%s(%s)");
        /**
         *  Z963 查詢列表增加 "測試租戶"(Y/N) ，預設是false 2020-04-17 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("testTenant").setDataMappingKey("testTenant")
                .addDataConvertion(true, "Y")
                .setDataConversionDefaultValue("N");
        sheetSetting.addColumnByModuleI18nKey("orderType").setDataMappingKey("orderType").setIntegerType()
                .addDataConvertion(1, DWModuleMessageResourceBundleUtils.getString("new"))
                .addDataConvertion(2, DWModuleMessageResourceBundleUtils.getString("renewed"))
                .addDataConvertion(3, DWModuleMessageResourceBundleUtils.getString("addingUsers"));


        sheetSetting.addColumnByModuleI18nKey("paymentDate").setDataMappingKey("payDate");
        sheetSetting.addColumnByModuleI18nKey("paymentNo").setDataMappingKey("payCode");
        /**
         * Z1402 在支付編號之後加入「拋轉日期」 2020-12-14 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("tossDate").setDataMappingKey("tossDate");
        DWExcelColumnSetting myCustomCellSetting = sheetSetting.addColumnByModuleI18nKey("orderDescription");
        myCustomCellSetting.getCellProcessor().setCellValueConverter(new MyCustomCellValueConversion());
        myCustomCellSetting.setDataMappingKeys(new String[]{"remark", "modifyReason"}, "%s / %s");
        /**
         * 新增remark備註 2020-02-26 Miko
         */
         
         /**
         * Z937 調整欄位 remark 是訂單說明/modifyReason 修改原因/comment 備註 2020-03-27 Miko
         */
         
        sheetSetting.addColumnByModuleI18nKey("comment").setDataMappingKey("comment");
        sheetSetting.addColumnByModuleI18nKey("invoiceNo").setDataMappingKey("invoiceCode");
        /**
         * 訂單來源做多語言處理 2020-05-08 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("orderSource").setDataMappingKey("orderSource")
                .addDataConvertion("DigiwinCloud", DWModuleMessageResourceBundleUtils.getString("DigiwinCloud"))
                .addDataConvertion("BossOnline", DWModuleMessageResourceBundleUtils.getString("BossOnline"))
                .addDataConvertion("BossOffline", DWModuleMessageResourceBundleUtils.getString("BossOffline"))
                .addDataConvertion("FIL", DWModuleMessageResourceBundleUtils.getString("FIL"))
                .addDataConvertion("HUAWEI", DWModuleMessageResourceBundleUtils.getString("HUAWEI"));
        sheetSetting.addColumnByModuleI18nKey("paymentType").setDataMappingKey("payTypeName");
        sheetSetting.addColumnByModuleI18nKey("departmentCode").setDataMappingKey("departCode");
        sheetSetting.addColumnByModuleI18nKey("empolyeeNo").setDataMappingKey("businessCode");
        /**
         *  Z963 推薦人部門跟推薦人工號、名稱三個欄位，匯出EXCEL時也要可以查看 2020-04-17 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("recommendedCode").setDataMappingKey("recommendedItCode");
        sheetSetting.addColumnByModuleI18nKey("recommendedName").setDataMappingKey("recommendedItName");
        sheetSetting.addColumnByModuleI18nKey("recommendedDepartmentCode").setDataMappingKey("recommendedDepartCode");
        /**
         *  falcon轉派要加"院校客戶"欄位，預設是false 2020-07-13 Miko
         */
        sheetSetting.addColumnByModuleI18nKey("school").setDataMappingKey("school")
                .addDataConvertion(true, "Y")
                .setDataConversionDefaultValue("N");

        sheetSetting.setDatas(datas);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();


        excelBuilder.create().write(bos);
        bos.close();
        byte[] bytes = bos.toByteArray();
        return bytes;
    }


    /**
     * My Custom Cell 值轉換器
     *
     * @author falcon
     */
    private class MyCustomCellValueConversion extends DWExcelCellValueBaseConverter {
        @Override
        public Object format(Object sourceValue) {
            List<Object> sourceData = (List<Object>) sourceValue;

            Object comment = sourceData.get(0) == null ? "" : sourceData.get(0);
            Object modifyReason = sourceData.get(1) == null ? "" : sourceData.get(1);

            if (comment.toString().length() > 0 || modifyReason.toString().length() > 0) {
                return super.format(sourceData);
            } else {
                return "";
            }
        }
    }

    /**
     * 訂單編號編號值轉換器
     *
     * @author Bomin
     */
    private class orderCodeValueConversion extends DWExcelCellValueBaseConverter {
        @Override
        public Object format(Object sourceValue) {
            List<Object> sourceData = (List<Object>) sourceValue;

            Double shopping = (Double) sourceData.get(0);
            String orderCode = sourceData.get(1) == null ? "" : (String) sourceData.get(1);
            String packCode = sourceData.get(2) == null ? "" : (String) sourceData.get(2);

            if (shopping == 2.0) {
                return packCode;
            }
            return orderCode;
        }
    }

    /**
     * 組合訂單號值轉換器
     *
     * @author Bomin
     */
    private class packCodeValueConversion extends DWExcelCellValueBaseConverter {
        @Override
        public Object format(Object sourceValue) {
            List<Object> sourceData = (List<Object>) sourceValue;

            Double shopping = (Double) sourceData.get(0);
            String packCode = sourceData.get(1) == null ? "" : (String) sourceData.get(1);

            if (shopping == 0.0) {
                return packCode;
            } else if (shopping == 1.0) {
                return "";
            } else if (shopping == 2.0) {
                return packCode;
            }
            return "";

        }
    }



    // 查詢客戶到期報表
    public Object getExpirationList(int pageNum, int pageSize, Map<String, Object> params) throws Exception {
        String gmcUrl = DWModuleConfigUtils.getCurrentModuleProperty("gmcUrl");
        String orderExpListApi = DWModuleConfigUtils.getCurrentModuleProperty("orderExpListApi");

        // header
        DWServiceContext context = DWServiceContext.getContext();
        Map<String, Object> requestHeader = context.getRequestHeader();
        String userToken = (String) requestHeader.get("token");

        if (params.containsKey("day")) {
            double doubleDay = (double) params.get("day"); // 轉型動作(透過Gson反序列化回來自動將Integer轉成Double
            int day = (int) doubleDay;
            params.put("day", day);
        }
        Gson gson = new Gson();
        String paramJson = gson.toJson(params);

        String encodedParams = URLEncoder.encode(paramJson, "UTF-8"); // 棄用了要記得調整
        /**
         * 調用GMC api 192.168.9.27:22615/api/gmc/v2/goods/willexpired/apps pageNum：
         * pageSize：設定顯示筆數 params：
         */
        String orderListUrl = String.format("%s%s?pageNum=%s&pageSize=%s&params=%s", gmcUrl, orderExpListApi,
                pageNum, pageSize, encodedParams);
        log.info(">>>get orderList url = " + orderListUrl);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet orderRequest = new HttpGet(orderListUrl);
        orderRequest.setHeader("Content-Type", "application/json");
        orderRequest.setHeader("digi-middleware-auth-user", userToken);
        HttpResponse orderResponse = client.execute(orderRequest);

        String orderContent = EntityUtils.toString(orderResponse.getEntity());
        int orderStatusCode = orderResponse.getStatusLine().getStatusCode();
        if (orderStatusCode != HttpStatus.SC_OK) {

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Get OrderService ExpirationList failed, status code = ").append(orderStatusCode)
                    .append(", please check the log for more information.");

            log.error("IOrderService.getOrderList failed! -> " + orderContent);

            throw new DWException(errorMessage.toString());
        }

        Map<String, Object> resultMap = DWGsonProvider.getGson().fromJson(orderContent, Map.class);
        List<Map<String, Object>> datas = (List<Map<String, Object>>) resultMap.get("list");
        for (Map<String, Object> datamap : datas) {
            String paymentType;
            if (datamap.containsKey("paymentType")) {
                if (datamap.get("paymentType").equals(0.0)) {
                    paymentType = DWModuleMessageResourceBundleUtils.getString("times");
                } else if (datamap.get("paymentType").equals(1.0)) {
                    paymentType = DWModuleMessageResourceBundleUtils.getString("personMonth");
                } else if (datamap.get("paymentType").equals(2.0)) {
                    paymentType = DWModuleMessageResourceBundleUtils.getString("timesMonth");
                } else if (datamap.get("paymentType").equals(3.0)) {
                    paymentType = DWModuleMessageResourceBundleUtils.getString("notControlled");
                } else if (datamap.get("paymentType").equals(4.0)) {
                    paymentType = DWModuleMessageResourceBundleUtils.getString("month");
                } else {
                    paymentType = datamap.get("paymentType").toString();
                }
                String count = datamap.get("count") + " " + paymentType;
                datamap.put("count", count);
            }
        }
        resultMap.put("success", true);
        resultMap.put("message", "get success");

        return resultMap;
    }

    // MaturityExcel到期報表
    public Object getExpirationExcel(Map<String, Object> params) throws Exception {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String fileName = "ExpOrderService.xlsx";
        DWServiceContext context = DWServiceContext.getContext();
        Map<String, Object> responseHeader = context.getResponseHeader();
        responseHeader.put("Content-Type", contentType);
        responseHeader.put("Content-disposition", "attachment;filename=" + fileName);

        Map<String, Object> map = (Map<String, Object>) this.getExpirationList(1, 9999999, params);
        List<Map<String, Object>> datas = (List<Map<String, Object>>) map.get("list");

        DWExcelBuilder excelBuilder = new DWExcelBuilder();
        DWExcelSheetSetting sheetSetting = excelBuilder.addSheet(DWModuleMessageResourceBundleUtils.getString("expSheetName"));

        sheetSetting.addColumnByModuleI18nKey("exptenantId").setDataMappingKey("tenantId");
        sheetSetting.addColumnByModuleI18nKey("exptenantName").setDataMappingKey("tenantName");
        sheetSetting.addColumnByModuleI18nKey("expgoodsName")
                .setDataMappingKeys(new String[]{"goodsName", "goodsCode"}, "%s(%s)");
        sheetSetting.addColumnByModuleI18nKey("priceUnit").setDataMappingKey("paymentTypeName");
        sheetSetting.addColumnByModuleI18nKey("buyUnumber(numberoftimes)").setDataMappingKey("count");

        sheetSetting.addColumnByModuleI18nKey("validityPeriod").setDataMappingKey("expiredTime");
        sheetSetting.addColumnByModuleI18nKey("expstrategyName").setDataMappingKey("sellingStrategyName");
        sheetSetting.addColumnByModuleI18nKey("countDown").setDataMappingKey("remainingDays").setIntegerType();
        sheetSetting.setDatas(datas);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        excelBuilder.create().write(bos);
        bos.close();
        byte[] bytes = bos.toByteArray();
        return bytes;
    }
}
