package com.digiwin.boss.dwreport.service.utils;

/**
 * Digiwin Excel Cell 值轉換器
 * @author falcon
 *
 */
public interface DWExcelCellValueConverter extends DWExcelCellValueFormatter {

	/**
	 * 設定轉換的默認值
	 * @param defaultValue 默認值
	 * @return this
	 */
	public void setConversionDefaultValue(Object defaultValue);
	
	/**
	 * 加入轉換值
	 * @param sourceValue 原始值
	 * @param conversionValue 轉換後的值
	 */
	public void addConversion(Object sourceValue, Object conversionValue);
	
	/**
	 * 轉換值
	 * @param columnSetting Column 設定
	 * @param cellSourceData Cell 來源數據
	 * @return 轉換後的 Cell 數據
	 */
	public Object convert(DWExcelColumnSetting columnSetting, Object cellSourceData) throws Exception;
}
