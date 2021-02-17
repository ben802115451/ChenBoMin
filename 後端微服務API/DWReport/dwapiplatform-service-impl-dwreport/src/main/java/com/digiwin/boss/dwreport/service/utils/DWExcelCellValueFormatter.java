package com.digiwin.boss.dwreport.service.utils;

/**
 * Digiwin Excel Cell value formatter
 * @author falcon
 *
 */
public interface DWExcelCellValueFormatter {

	/**
	 * 設定格式
	 * @param format 格式
	 */
	public void setFormat(String format);
	
	/**
	 * 取得格式
	 * @return 格式
	 */
	public String getFormat();
	
	/**
	 * 格式 Cell Value
	 * @param cellSourceData Cell 來源數據
	 * @return 格式化的值
	 */
	public Object format(Object cellSourceData);
}
