package com.digiwin.boss.dwreport.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.data.DWDataColumnCollection;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.module.spring.SpringContextUtils;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Miko
 */
public class ExcludeOrderServiceTest {
    /**
     * HTTP相關
     */
    @Mocked
    HttpClient httpClient;

    @Mocked
    CloseableHttpClient mockCloseableHttpClient;

    @Mocked
    HttpUriRequest httpUriRequest;

    @Mocked
    HttpResponse httpResponse;

    @Mocked
    StatusLine statusLine;

    @Mocked
    HttpEntity httpEntity;

    @Mocked
    CloseableHttpResponse mockCloseableHttpResponse;

    @Mocked
    HttpGet httpGet;

    @Mocked
    HttpClientBuilder httpClientBuilder;


    @Tested
    ExcludeOrderService excludeOrderService;

    @Injectable
    SalesStatisticsService salesStatisticsService;

    DWDataSet expected_dataset;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dwDaoImpl = new DWDaoImpl(queryRunner);

    @Mocked
    DWMetadataContainer mockDWMetadataContainer;

    @Mocked
    DWDataColumnCollection mockDWDataColumnCollection;

    @Mocked
    DWSQLExecutionResult mockDWSQLExecutionResult;

    @Mocked
    SpringContextUtils mockSpringContextUtils;

    @Test
    public void post() throws Exception {
        String tableName = "exclude_order";
        String mockOrderCode = "T20050600001";
        String ORDER_CODE = "orderCode";
        DWDataRow dataRow = new DWDataSet().newTable(tableName).newRow().set(ORDER_CODE, mockOrderCode);

        //讀取成功呼叫/api/omc/v2/orders/code/{orderCode}回來的content
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-orderCodeContent.json")));
        StringBuilder jsonText = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            jsonText.append(line);
        }

        new Expectations() {
            {
                new MockUp<ExcludeOrderService>() {
                    @Mock
                    public void process(List<String> orderCodeList) throws Exception {

                    }
                };

                dwDaoImpl.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                result = mockDWSQLExecutionResult;
            }
        };

        //場景1：orderCode有值、呼叫omc status 200
        DWServiceResult result = (DWServiceResult) excludeOrderService.post(dataRow.getDWDataSet());
        assertEquals(true, result.getSuccess());

        //場景2：orderCode為null
        DWDataRow dataRow1 = new DWDataSet().newTable(tableName).newRow().set(ORDER_CODE, null);
        assertThrows(DWArgumentException.class, () -> excludeOrderService.post(dataRow1.getDWDataSet()));
    }

    @Test
    public void put() throws Exception {
        String tableName = "exclude_order";
        String mockOrderCode = "T20050600001";
        String ORDER_CODE = "orderCode";
        DWDataRow dataRow = new DWDataSet().newTable(tableName).newRow().set(ORDER_CODE, mockOrderCode);

        new Expectations() {
            {
                dwDaoImpl.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                result = mockDWSQLExecutionResult;
            }
        };

        //場景1：orderCode有值
        DWServiceResult result = (DWServiceResult) excludeOrderService.put(dataRow.getDWDataSet());
        assertEquals(true, result.getSuccess());
        assertEquals("修改成功", result.geMessage());

        //場景2：orderCode為null
        DWDataRow dataRow1 = new DWDataSet().newTable(tableName).newRow().set(ORDER_CODE, null);
        assertThrows(DWArgumentException.class, () -> excludeOrderService.put(dataRow1.getDWDataSet()));
    }

    @Test
    public void delete() throws Exception {
        String tableName = "exclude_order";
        String mockOrderCode = "T20050600001";
        String ORDER_CODE = "orderCode";
        DWDataRow dataRow = new DWDataSet().newTable(tableName).getRows().newRow().set(ORDER_CODE, mockOrderCode);
        dataRow.delete();
        dataRow.getDataTable().getRows().getIteratorOfRemovedRows();

        //讀取成功呼叫/api/omc/v2/orders/code/{orderCode}回來的content
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-orderCodeContent.json")));
        StringBuilder jsonText = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            jsonText.append(line);
        }

        new Expectations() {
            {
                new MockUp<ExcludeOrderService>() {
                    @Mock
                    public void process(List<String> orderCodeList) throws Exception {

                    }
                };

                dwDaoImpl.execute((DWDataSet) any, (DWDataSetOperationOption)any);
                result = mockDWSQLExecutionResult;
            }
        };

        //場景1：orderCode有值、呼叫omc status 200
        DWServiceResult result = (DWServiceResult) excludeOrderService.delete(dataRow.getDWDataSet());
        assertEquals(true, result.getSuccess());
    }

    @Test
    public void get() throws Exception {
        new Expectations() {
            {
                dwDaoImpl.select((DWPagableQueryInfo) any, (DWDataSetOperationOption)any);
                result = expected_dataset;
            }
        };

        DWServiceResult result = (DWServiceResult) excludeOrderService.get();
        assertEquals(true, result.getSuccess());
    }


    @Test
    public void process() throws Exception {
        //讀取成功呼叫/api/omc/v2/orders/code/{orderCode}回來的content
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-orderCodeContent.json")));
        StringBuilder jsonText = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            jsonText.append(line);
        }

        String mockDate = "2020-04-24";
        new Expectations() {
            {
                HttpEntity entity = new StringEntity(jsonText.toString(), "UTF-8");
                httpResponse.getEntity();
                result = entity;

                httpResponse.getStatusLine();
                result = statusLine;

                statusLine.getStatusCode();
                result = 200;

                salesStatisticsService.putAssignedDate(mockDate);
                result = "Success!  A total of 6 new data";
            }
        };

        String mockOrderCode = "T20050600001";
        List<String> orderCodeList = new ArrayList<>();
        orderCodeList.add(mockOrderCode);

        //場景1：orderCodeList值正確，呼叫/api/omc/v2/orders/code/{orderCode} statusCode拿到200
        excludeOrderService.process(orderCodeList);

        //場景2：orderCodeList值正確，呼叫/api/omc/v2/orders/code/{orderCode} statusCode拿到500(即非200)
        new Expectations() {
            {
                HttpEntity entity = new StringEntity(jsonText.toString(), "UTF-8");
                httpResponse.getEntity();
                result = entity;

                httpResponse.getStatusLine();
                result = statusLine;

                statusLine.getStatusCode();
                result = 500;
            }
        };

        //Get orderCodeApi failed, status code = 500, please check the log for more information.
        assertThrows(DWException.class, () -> excludeOrderService.process(orderCodeList));

        //場景3：orderCodeList值正確，呼叫/api/omc/v2/orders/code/{orderCode} statusCode拿到200，但orderStatus為unpaid
        BufferedReader unpaidBufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-orderCodeContentUnpaid.json")));
        StringBuilder unpaidJsonText = new StringBuilder();
        String unpaidLine;
        while ((unpaidLine = unpaidBufferedReader.readLine()) != null) {
            unpaidJsonText.append(unpaidLine);
        }

        new Expectations() {
            {
                HttpEntity entity = new StringEntity(unpaidJsonText.toString(), "UTF-8");
                httpResponse.getEntity();
                result = entity;

                httpResponse.getStatusLine();
                result = statusLine;

                statusLine.getStatusCode();
                result = 200;
            }
        };

        //T20050600001 該張訂單未付款，請輸入已付款的訂單編號！
        assertThrows(DWException.class, () -> excludeOrderService.process(orderCodeList));

        //場景4：orderCodeList值正確，呼叫/api/omc/v2/orders/code/{orderCode} statusCode拿到200，但code非200(即orderCode不在OMC中)
        BufferedReader failBufferedReader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-orderCodeContentFail.json")));
        StringBuilder failJsonText = new StringBuilder();
        String failLine;
        while ((failLine = failBufferedReader.readLine()) != null) {
            failJsonText.append(failLine);
        }

        new Expectations() {
            {
                HttpEntity entity = new StringEntity(failJsonText.toString(), "UTF-8");
                httpResponse.getEntity();
                result = entity;

                httpResponse.getStatusLine();
                result = statusLine;

                statusLine.getStatusCode();
                result = 200;
            }
        };

        //T20050600001 not exist!
        assertThrows(DWException.class, () -> excludeOrderService.process(orderCodeList));
    }
}