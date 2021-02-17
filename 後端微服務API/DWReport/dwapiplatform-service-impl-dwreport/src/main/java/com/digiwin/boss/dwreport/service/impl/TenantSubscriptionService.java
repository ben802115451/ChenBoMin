package com.digiwin.boss.dwreport.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;

import com.digiwin.app.data.DWDataSetOperationOption;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.boss.dwreport.dao.DBConstants;
import com.digiwin.boss.dwreport.dao.SqlWhereCondition;
import com.digiwin.boss.dwreport.permission.DataPermissionService;
import com.digiwin.boss.dwreport.service.ITenantSubscriptionService;

public class TenantSubscriptionService implements ITenantSubscriptionService {

	@Autowired
	@Qualifier("Dao")
	private DWDao dao;

	@Override
	public Object get(int pageNum, int pageSize, Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<>();
		long total = 0;
		
		Map<String, Object> dataPermissionResult = getDataPermission();
		if(!(boolean)dataPermissionResult.get("HaveData")){
			result.put("message", "沒有配置數據權限資料");
		}else {
			data = query(pageNum, pageSize, params, dataPermissionResult);
			total = queryTotal(params, dataPermissionResult);
			result.put("message", "get success");
		}
		
		result.put("list", data);
		result.put("total", total);
		result.put("success", true);

		return result;
	}

	@Override
	public Object getFile(Map<String, Object> params) throws Exception {
		String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		String fileName = "TenantSubscriptionService.xlsx";
		DWServiceContext context = DWServiceContext.getContext();
		Map<String, Object> responseHeader = context.getResponseHeader();
		responseHeader.put("Content-Type", contentType);
		responseHeader.put("Content-disposition", "attachment;filename=" + fileName);

		byte[] bytes = null;
		SXSSFWorkbook wb = null;
		ByteArrayOutputStream bos = null;
		int headerRowIndex = 0;

		String module = DWServiceContext.getContext().getModuleName();
		int pageSize = Integer.parseInt(DWModuleConfigUtils.getProperty(module, "dwreportExecelBatchRows"));

		try {
			wb = new SXSSFWorkbook(100);
			Sheet sheet = wb.createSheet("租戶訂閱明細");
			generateHeaderRow(headerRowIndex, sheet);
			long total = 0;
			
			Map<String, Object> dataPermissionResult = getDataPermission();
				total = queryTotal(params,dataPermissionResult);
			long remainder = total % pageSize;
			long pageCount = total / pageSize;
			if (remainder != 0) {
				pageCount = pageCount + 1;
			}
			int rowIndex = 1;
			for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
				List<Map<String, Object>> datas = query(pageNum, pageSize, params,dataPermissionResult);
				for (Map<String, Object> data : datas) {
					generateDataRow(rowIndex, sheet, data);
					rowIndex++;
				}
			}

			bos = new ByteArrayOutputStream();
			wb.write(bos);
			bytes = bos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (bos != null) {
				bos.close();
			}
			if (wb != null) {
				wb.close();
			}
		}

		return bytes;
	}

	private List<Map<String, Object>> query(int pageNum, int pageSize, Map<String, Object> params,Map<String, Object> dataPermissionResult) throws Exception {
		List<Map<String, Object>> result = null;
		List<Object> sqlParams = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(DBConstants.SQL_SELECT_FILEDS);

			Map<String, Object> dataPermissionCondition = dataPermissionResult;
			sql.append(" " + dataPermissionCondition.get("sql"));
			sqlParams.addAll((Collection<?>) dataPermissionCondition.get("goodsCode"));

		SqlWhereCondition whereCondition = generateSqlWhereCondition(params);
		sql.append(" " + whereCondition.getSql());
		sqlParams.addAll(whereCondition.getSqlParams());

		sql.append(" " + DBConstants.SQL_ORDER_BY);
		sql.append(" " + DBConstants.SQL_LIMIT);
		int startIndex = pageSize * (pageNum - 1);
		sqlParams.add(startIndex);
		sqlParams.add(pageSize);

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		result = dao.select(option, sql.toString(), sqlParams.toArray());
		result = adjustData(result);
		return result;
	}

	private long queryTotal(Map<String, Object> params,Map<String, Object> dataPermissionResult) throws Exception {
		List<Map<String, Object>> result = null;
		List<Object> sqlParams = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();

		sql.append(DBConstants.SQL_SELECT_COUNT);

			Map<String, Object>  dataPermissionCondition = dataPermissionResult;
			sql.append(" " + dataPermissionCondition.get("sql"));
			sqlParams.addAll((Collection<?>) dataPermissionCondition.get("goodsCode"));

		SqlWhereCondition whereCondition = generateSqlWhereCondition(params);
		sql.append(" " + whereCondition.getSql());
		sqlParams.addAll(whereCondition.getSqlParams());

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		result = dao.select(option, sql.toString(), sqlParams.toArray());
		long rows = Long.parseLong(result.get(0).get("total").toString());
		return rows;
	}

	private List<Map<String, Object>> adjustData(List<Map<String, Object>> data) throws Exception {

		for (Map<String, Object> dataRow : data) {
			String transactionStatus = getTransactionStatus(dataRow);
			dataRow.put("transactionStatus", transactionStatus);
		}

		return data;
	}

	private String getTransactionStatus(Map<String, Object> data) throws Exception {
		String result = "";
		Object sid = data.get("sid");
		Object tenantId = data.get("tenantId");
		Object goodsCode = data.get("goodsCode");
		Object createtime = data.get("createtime");
		StringBuilder sql = new StringBuilder();
		sql.append(DBConstants.SQL_SELECT_TRANSACTION_STATUS);
		SqlWhereCondition condition = new SqlWhereCondition();
		generateSqlWhereConditionPaid(condition);
		condition.appenSql(" " + DBConstants.SQL_WHERE_SID);
		condition.addSqlParams(sid);
		condition.appenSql(" " + DBConstants.SQL_WHERE_TENANTID);
		condition.addSqlParams(tenantId);
		condition.appenSql(" " + DBConstants.SQL_WHERE_GOODS_CODE);
		condition.addSqlParams(goodsCode);
		condition.appenSql(" " + DBConstants.SQL_WHERE_CREATETIME);
		condition.addSqlParams(createtime);
		sql.append(" " + condition.getSql());

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		List<Map<String, Object>> checkOrderData = dao.select(option, sql.toString(), condition.getSqlParams().toArray());
		long rows = Long.parseLong(checkOrderData.get(0).get("total").toString());

		if (rows == 0) {
			BigDecimal zero = new BigDecimal(0).setScale(2);			
			BigDecimal amount = (BigDecimal) data.get("amount");
			if (amount.equals(zero)) {
				result = "第一次免費";
			} else {
				result = "第一次付費";
			}
		} else {
			result = "續購";
		}
		return result;
	}

	private void generateSqlWhereConditionPaid(SqlWhereCondition result) {
		String orderStatusPaid = "1";
		result.appenSql(" " + DBConstants.SQL_WHERE_ORDER_STATUS_FIXED);
		result.addSqlParams(orderStatusPaid);
	}

	private SqlWhereCondition generateSqlWhereCondition(Map<String, Object> params) throws Exception {
		SqlWhereCondition result = new SqlWhereCondition();

		generateSqlWhereConditionPaid(result);

		String tenantId = trimValue(params.get("tenantId"));
		if (tenantId != null) {
			result.appenSql(" " + DBConstants.SQL_WHERE_TENANTID_TENANTNAME);
			result.addSqlParams("%" + tenantId + "%");
			result.addSqlParams("%" + tenantId + "%");
		}

		String productType = trimValue(params.get("productType"));
		if (productType != null) {
			result.appenSql(" " + DBConstants.SQL_WHERE_PRODUCT_TYPE);
			result.addSqlParams(productType);

			String productTypeCode = productType;
			if ("應用".equals(productType)) {
				productTypeCode = "app";
			}
			if ("服務".equals(productType)) {
				productTypeCode = "service";
			}
			if ("課程".equals(productType)) {
				productTypeCode = "course";
			}

			result.addSqlParams(productTypeCode);
		}

		String goodsCode = trimValue(params.get("goodsCode"));
		if (goodsCode != null) {
			result.appenSql(" " + DBConstants.SQL_WHERE_GOODS_CODE_NAME);
			result.addSqlParams(goodsCode);
			result.addSqlParams(goodsCode + "%");
		}

		String strategyName = trimValue(params.get("strategyName"));
		if (strategyName != null) {
			result.appenSql(" " + DBConstants.SQL_WHERE_STRATEGY_NAME);
			result.addSqlParams(strategyName + "%");
		}

//		String orderStatus = trimValue(params.get("orderStatus"));
//		if (orderStatus != null) {
//			result.appenSql(" " + DBConstants.SQL_WHERE_ORDER_STATUS);
//			result.addSqlParams(orderStatus);
//
//			String orderStatusCode = orderStatus;
//			if ("未支付".equals(orderStatus)) {
//				orderStatusCode = "Unpaid";
//			}
//			if ("修改價格".equals(orderStatus)) {
//				orderStatusCode = "Modify";
//			}
//			if ("已支付".equals(orderStatus)) {
//				orderStatusCode = "Paid";
//			}
//			if ("已退款".equals(orderStatus)) {
//				orderStatusCode = "Refund";
//			}
//
//			result.addSqlParams(orderStatusCode);
//		}

		String beginTime = trimValue(params.get("beginTime"));
		if (beginTime != null) {
			result.appenSql(" " + DBConstants.SQL_WHERE_BEGIN_TIME);
			result.addSqlParams(beginTime);
		}

		String endTime = trimValue(params.get("endTime"));
		if (endTime != null) {
			result.appenSql(" " + DBConstants.SQL_WHERE_END_TIME);
			result.addSqlParams(endTime);
		}

		return result;
	}

	private String trimValue(Object value) throws Exception {
		return trimValue(value, null);
	}

	private String trimValue(Object value, String defaultValue) throws Exception {
		String result = defaultValue;

		if (value != null) {
			result = String.valueOf(value);
			result = result.trim();
			if ("".equals(result)) {
				result = defaultValue;
			}
		}

		return result;
	}

	private void generateHeaderRow(int headerRowIndex, Sheet sheet) {
		Row headerRow = sheet.createRow(headerRowIndex);
		Cell tenantIdCell = headerRow.createCell(0);
		tenantIdCell.setCellValue("租戶名稱 (ID)");
		Cell productCell = headerRow.createCell(1);
		productCell.setCellValue("產品名稱 (ID)");
		Cell strategyCell = headerRow.createCell(2);
		strategyCell.setCellValue("銷售方案");
		Cell ordercodeCell = headerRow.createCell(3);
		ordercodeCell.setCellValue("訂單單號");
		Cell createTimeCell = headerRow.createCell(4);
		createTimeCell.setCellValue("訂單日期");
		Cell orderStatus = headerRow.createCell(5);
		orderStatus.setCellValue("訂單狀態");
		Cell amountCell = headerRow.createCell(6);
		amountCell.setCellValue("金額");
		Cell payPriceCell = headerRow.createCell(7);
		payPriceCell.setCellValue("應付金額");
		Cell transactionStatusCell = headerRow.createCell(8);
		transactionStatusCell.setCellValue("交易狀態");
	}

	private void generateDataRow(int rowIndex, Sheet sheet, Map<String, Object> data) throws Exception {
		Row row = sheet.createRow(rowIndex);

		Cell tenantCell = row.createCell(0);
		String tenantId = trimValue(data.get("tenantId"), "");
		String tenantName = trimValue(data.get("tenantName"), "");
		tenantCell.setCellValue(tenantName + "(" + tenantId + ")");

		Cell goodsCell = row.createCell(1);
		String goodsCode = trimValue(data.get("goodsCode"), "");
		String goodsName = trimValue(data.get("goodsName"), "");
		goodsCell.setCellValue(goodsName + "(" + goodsCode + ")");

		Cell strategyNameCell = row.createCell(2);
		String strategyName = trimValue(data.get("strategyName"), "");
		strategyNameCell.setCellValue(strategyName);
		
		Cell ordercodeNameCell = row.createCell(3);
		String ordercode = String.valueOf(data.get("ordercode"));
		ordercodeNameCell.setCellValue(ordercode);

		Cell createTimeCell = row.createCell(4);
		String createTime = trimValue(data.get("createTime"), "");
		createTimeCell.setCellValue(createTime);

		Cell orderStatusCell = row.createCell(5);
		String orderStatus = trimValue(data.get("orderStatus"), "");
//		if ("0".equals(orderStatus)) {
//			orderStatus = "未支付";
//		}
//		if ("Modify".equals(orderStatus)) {
//			orderStatus = "修改價格";
//		}
		if ("1".equals(orderStatus)) {
			orderStatus = "已付款";
		}
//		if ("0".equals(orderStatus)) {
//			orderStatus = "已退款";
//		}
		orderStatusCell.setCellValue(orderStatus);

		Cell amountCell = row.createCell(6);
		String amount = trimValue(data.get("amount"), "");
		amountCell.setCellValue(amount);
		
		Cell payPriceCell = row.createCell(7);
		String payPrice = trimValue(data.get("payPrice"), "");
		payPriceCell.setCellValue(payPrice);

		Cell transactionStatusCell = row.createCell(8);
		String transactionStatus = trimValue(data.get("transactionStatus"), "");
		transactionStatusCell.setCellValue(transactionStatus);

	}
	
	private SqlWhereCondition generateDataPermissionCondition(Set<String> goodsCodes) throws Exception {
		SqlWhereCondition result = new SqlWhereCondition();
		StringBuilder inSql = new StringBuilder();
		for(String goodsCode: goodsCodes) {
			inSql.append("?,");
			result.addSqlParams(goodsCode);
		}
		String inSqlWithOutFinalDot = inSql.toString().substring(0, inSql.toString().length() - 1);
		result.appenSql(" " + DBConstants.SQL_WHERE_GOODS_CODE_IN.replaceAll("@args@", inSqlWithOutFinalDot));

		return result;
	}
	
	private Map<String, Object> getDataPermission() throws Exception{
		
		String token = DWServiceContext.getContext().getToken();

		Map<String, Object> permissionData = DataPermissionService.getDataPermissionMap();

		return permissionData;
		
	}
	

}
