package com.digiwin.deploy.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.digiwin.app.dao.filter.DWSQLManagementFieldFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWQueryInfoBuilder;
import com.digiwin.app.dao.DWQueryOrderby;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.data.DWDataRow;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetBuilder;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.data.exceptions.DWDataTableNotFoundException;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.deploy.service.IDeployService;

public class DeployService implements IDeployService {

	// 作業系統環境
	static final String LINUX = "linux";
	static final String WINDOWS = "windows";
	// 上傳的檔案類型
	static final String SQL = "SQL";
	static final String TAR = "tar";
	static final String IMAGE = "image";
	static final String ZIP = "zip";

	@Autowired
	@Qualifier("Dao")
	private DWDao dao;

	// 新增產品線佈署資訊
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
	public Object post(DWDataSet dataset) throws Exception {

		// 單身表邏輯
		DWDataTable table = dataset.getTable(DBConstants.DEPLOY_DETAIL);
		for (DWDataRow row : table.getRows()) {

			this.checkDeployDetail(row);
		}

		// 更新表的管理字段
		this.updateManagementColumn(dataset, DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL,
				DBConstants.DEPLOY_PARAMETERS);

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		// 第一層帶到第二層測試
		option.getInsertOption().getAutoIncrementOption().addSource(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL);
		option.getInsertOption().getAutoIncrementOption().addSource(DBConstants.DEPLOY_INFO,
				DBConstants.DEPLOY_PARAMETERS);
		option.setManagementFieldEnabled(false);

		DWSQLExecutionResult result = dao.execute(dataset, option);

		return DWServiceResultBuilder.build("新增成功", result);
	}

	// 修改產品線佈署資訊

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
	public Object put(DWDataSet dataset) throws Exception {

		List<Object> updateCountDetailIdList = new ArrayList<Object>();
		StringBuilder updateCountSql = new StringBuilder();

		try {

			// 單身表邏輯
			DWDataTable table = dataset.getTable(DBConstants.DEPLOY_DETAIL);

			for (DWDataRow row : table.getRows()) {

				this.checkDeployDetail(row);

				if (row.isUpdate()) {

					updateCountDetailIdList.add(row.get(DBConstants.DEPLOY_DETAIL_ID));

					updateCountSql.append("update ").append(DBConstants.DEPLOY_DETAIL).append(" set ")
							.append(DBConstants.UPDATE_COUNT).append(" = ").append(DBConstants.UPDATE_COUNT)
							.append(" + 1").append(" where deploy_detail_id = ?;");
				}
			}
		} catch (DWDataTableNotFoundException e) {

			// 找不到表 不做事
		}

		// 更新表的管理字段
		this.updateManagementColumn(dataset, DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL,
				DBConstants.DEPLOY_PARAMETERS);

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		Object result = this.dao.execute(dataset, option);

		// 更新單身更新次數
		if (updateCountDetailIdList.size() > 0) {

			this.dao.update(option, updateCountSql.toString(), updateCountDetailIdList.toArray());
		}

		return DWServiceResultBuilder.build(result);
	}

	/**
	 * 檢查單身表
	 * 
	 * @param dataset 數據集
	 * @throws Exception 異常
	 */
	private void checkDeployDetail(DWDataRow row) throws Exception {

		if (row.isDeleted())
			return;

		String fileType = (String) row.get(DBConstants.FILE_TYPE);
		if (fileType == null || fileType.isEmpty()) {

			throw new DWArgumentException("fileType", "fileType is null or empty!");
		}

		String osEnvironment = (String) row.get(DBConstants.OS_ENVIROMENT);
		if (osEnvironment == null || osEnvironment.isEmpty()) {
			throw new DWArgumentException("osEnvironment", "osEnvironment is null or empty!");
		}
		// 檢查名稱
		String fileName = (String) row.get(DBConstants.FILE_NAME);
		if (fileName == null || fileName.isEmpty()) {
			throw new DWArgumentException("fileName", "fileName is null or empty!");
		}

		if (LINUX.equalsIgnoreCase(osEnvironment)) {
			// SQL、TAR之外的都拋錯
			// 7/27 Windows環境改成只能傳SQL檔和TAR檔
			if (!fileType.equalsIgnoreCase(SQL) && !fileType.equalsIgnoreCase(TAR)) {
				throw new DWBusinessException("檔案名稱：" + fileName + "的檔案類型錯誤，Linux只能上傳TAR檔和SQL檔");
			}
		}
		if (WINDOWS.equalsIgnoreCase(osEnvironment)) {
			// SQL、zip之外的都拋錯
			// 7/27 Windows環境改成只能傳SQL檔和ZIP檔
			if (!fileType.equalsIgnoreCase(SQL) && !fileType.equalsIgnoreCase(ZIP)) {
				throw new DWBusinessException("檔案名稱：" + fileName + "的檔案類型錯誤，Windows只能上傳zip檔和SQL檔");
			}
		}
	}

	/**
	 * 更新管理字段
	 * 
	 * @param dataset   數據集
	 * @param tableName 表名
	 * @throws Exception 異常
	 */
	private void updateManagementColumn(DWDataSet dataset, String... tableNames) throws Exception {

		String userId = this.getUserId();

		DWDataTable targetTable = null;

		for (String tableName : tableNames) {

			try {

				targetTable = dataset.getTable(tableName);
			} catch (DWDataTableNotFoundException e) {

				// 找不到表 不做事
				continue;
			}

			// 更新表中的管理字段
			targetTable = dataset.getTable(tableName);
			targetTable.getRows().stream().forEach(row -> {

				if (row.isNew()) {

					row.set(DBConstants.CREATE_USER, userId);
					row.set(DBConstants.MODIFY_USER, userId);
				} else if (row.isUpdate()) {

					row.set(DBConstants.MODIFY_USER, userId);
				}
			});
		}
	}

	// 取得目前AppCode列表(單身)
	@Override
	public Object getDetailList(List<Object> oids) throws Exception {
		// 連動查詢
		DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
		DWQueryInfo queryInfo = queryInfoBuilder.setOids(oids).setPrimaryKeyName(DBConstants.DEPLOY_ID).create();
		queryInfo.setTableName(DBConstants.DEPLOY_INFO);

		// 設定連動查詢信息
		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.addCascadeQuery(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL);
		option.addCascadeQuery(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_PARAMETERS);
		option.setManagementFieldEnabled(false);

		DWDataSet dataset = this.dao.select(queryInfo, option);

		return DWServiceResultBuilder.build(dataset);
	}

	// 刪除產品線佈署
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
	public Object delete(List<Object> oids) throws Exception {
		if (oids == null || oids.isEmpty())
			throw new DWArgumentException("id", "id is null or empty!");

		DWDataSetBuilder builder = new DWDataSetBuilder();
		DWDataSet dataset = builder.addTable(DBConstants.DEPLOY_INFO).setDeletedOids(oids).createDataSet();

		// 連動刪除
		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.addCascadeDeleting(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_DETAIL);
		option.addCascadeDeleting(DBConstants.DEPLOY_INFO, DBConstants.DEPLOY_PARAMETERS);
		option.setManagementFieldEnabled(false);

		Object result = this.dao.execute(dataset, option);
		return DWServiceResultBuilder.build("產品線佈署刪除成功", result);

	}

	// 移除佈署檔案
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
	public Object deleteDeployFile(DWDataSet dataset) throws Exception {

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		Object result = this.dao.execute(dataset, option);

		return DWServiceResultBuilder.build("檔案刪除成功", result);
	}

	// 發佈(指定)正式首佈版本
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
	public Object putIsPublish(String oid) throws Exception {

		// 檢查欄位不能為空值
		if (oid == null || oid.isEmpty())
			throw new DWArgumentException("oid", "oid is null or empty!");

		// 把字串id轉成list丟給setOids
		List<Object> strsToList = Arrays.asList(oid);
		DWQueryInfoBuilder queryInfoBuilder = new DWQueryInfoBuilder();
		DWQueryInfo queryInfo = queryInfoBuilder.setOids(strsToList).setPrimaryKeyName(DBConstants.DEPLOY_ID).create();
		queryInfo.setTableName(DBConstants.DEPLOY_INFO);

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		DWDataRow row = this.dao.selectOne(queryInfo, option);
		if (row == null) {
			throw new DWBusinessException(String.format("找不到指定的版本 oid = %s", oid));
		}
		if(row.get(DBConstants.PUBLISH_STATUS).equals("1")){
			throw new DWBusinessException("此版本已發佈");
		}
		Object appId = row.get(DBConstants.APP_ID);
		Object deployArea = row.get(DBConstants.DEPLOY_AREA);
		Object deploySite = row.get(DBConstants.DEPLOY_SITE);

		String updateTo2Sql = "UPDATE deploy_info SET publish_status = 2 WHERE publish_status = 1 AND app_id=? and deploy_area =? AND deploy_site = ? ";

		String updateTo1Sql = "UPDATE deploy_info "
			    + "SET publish_time = "
			    + "CASE WHEN publish_status = 0 THEN NOW() ELSE publish_time END, " //狀態碼0、1、2，非0的時候，publish_time設回原值
			    + "publish_status = 1, "
			    + "recent_time = NOW() " //recent_time邏輯為：只要是1就壓上NOW()時間
			    + "WHERE deploy_id = ?";	
		
		dao.update(option, updateTo2Sql, appId, deployArea, deploySite);
		Object result = dao.update(option, updateTo1Sql, oid);
		return DWServiceResultBuilder.build(result);
	}

	/**
	 * 取得目前用戶編號
	 * 
	 * @return 目前用戶編號
	 * @throws Exception 異常
	 */
	private String getUserId() throws Exception {

		Map<String, Object> profile = DWServiceContext.getContext().getProfile();
		// 取得使用者的名稱
		String userId = (String) profile.get("userId");

		return userId;
	}

	@Override
	public Object getList(DWPagableQueryInfo queryInfo) throws Exception {
		queryInfo.setTableName("deploy_info");

		DWDataSetOperationOption option = new DWDataSetOperationOption();
		option.setManagementFieldEnabled(false);

		Object result = this.dao.selectWithPage(queryInfo, option);

		return DWServiceResultBuilder.build(result);
	}
}
