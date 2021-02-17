package com.digiwin.boss.dwreport.service.utils;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Digiwin Excel Cell 處理器
 * @author falcon
 *
 */
public interface DWExcelCellProcessor {
	
	/**
	 * 設定 Cell Value 轉換器
	 */
	public void setCellValueConverter(DWExcelCellValueConverter converter);
	/**
	 * 取得 Cell Value 轉換器
	 * @return Cell Value 轉換器
	 */
	public DWExcelCellValueConverter getCellValueConverter();
	/**
	 * 設定 Cell 裝飾器
	 * @param decorator 裝飾器
	 */
	public void setCellDecorator(DWExcelCellDecorator decorator);
	/**
	 * 取得 Cell 裝飾器
	 * @return Cell 裝飾器
	 */
	public DWExcelCellDecorator getCellDecorator();

	/**
	 * 處理
	 * @param cell 數據格
	 * @param columnSetting Column 設定
	 * @param rowData Row 數據
	 */
	public void process(Cell cell, DWExcelColumnSetting columnSetting, Map<String, Object> rowData) throws Exception;
}
