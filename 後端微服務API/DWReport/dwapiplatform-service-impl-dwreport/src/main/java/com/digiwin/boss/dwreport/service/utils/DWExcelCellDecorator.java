package com.digiwin.boss.dwreport.service.utils;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Digiwin Excel Cell 樣式裝飾器
 * @author falcon
 *
 */
public interface DWExcelCellDecorator {

	/**
	 * 樣式化
	 * @param cell Cell
	 * @param columnSetting Column 設定
	 * @param sourceValues 來源值
	 * @param cellValue Cell 值
	 */
	public void style(Cell cell, DWExcelColumnSetting columnSetting, Object sourceValues, Object cellValue) throws Exception;
}
