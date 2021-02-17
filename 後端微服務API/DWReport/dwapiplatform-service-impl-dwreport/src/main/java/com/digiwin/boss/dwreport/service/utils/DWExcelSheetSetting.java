package com.digiwin.boss.dwreport.service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;

/**
 * Digiwin Excel Sheet 設定
 * @author falcon
 *
 */
public class DWExcelSheetSetting {

	/**
	 * Sheet 名稱
	 */
	private String sheetName = null;
	/**
	 * Column 設定
	 */
	private List<DWExcelColumnSetting> columns = new ArrayList<>();
	/**
	 * 數據
	 */
	private List<Map<String, Object>> datas;
	
	/**
	 * 設定 sheet 名稱
	 * @param sheetName sheet 名稱
	 * @return sheet 設定
	 */
	public DWExcelSheetSetting setSheetName(String sheetName) {
		
		this.sheetName = sheetName;
		
		return this;
	}
	/**
	 * 取得 Sheet 名稱
	 * @return Sheet 名稱
	 */
	public String getSheetName() {
		
		return this.sheetName;
	}
	/**
	 * 加入 Column 設定
	 * @param title 標題
	 * @return Column 設定
	 */
	public DWExcelColumnSetting addColumn(String title) {
		
		DWExcelColumnSetting setting = new DWExcelColumnSetting();
		this.columns.add(setting);
		
		return setting.setTitle(title);
	}
	/**
	 * 加入 Column 設定 (標題使用 I18n 鍵值)
	 * @param titleI18nKey 標題的 I18n 鍵值
	 * @return Column 設定
	 */
	public DWExcelColumnSetting addColumnByModuleI18nKey(String titleI18nKey) {
		
		String title = DWModuleMessageResourceBundleUtils.getString(titleI18nKey);
		
		return this.addColumn(title);
	}
	/**
	 * 取得 Column 設定
	 * @return
	 */
	public List<DWExcelColumnSetting> getColumns() {
		
		return this.columns;
	}
	/**
	 * 設定 數據
	 * @param datas 數據
	 * @return Sheet 設定
	 */
	public DWExcelSheetSetting setDatas(List<Map<String, Object>> datas) {

		this.datas = datas;
		
		return this;
	}
	/**
	 * 取得數據
	 * @return 數據
	 */
	public List<Map<String, Object>> getDatas() {
		
		if (this.datas == null) this.datas = Collections.emptyList();
		
		return this.datas;
	}
}
