package com.digiwin.boss.dwreport.service.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Digiwin Excel Cell 基礎值轉換器
 * @author falcon
 *
 */
public class DWExcelCellValueBaseConverter implements DWExcelCellValueConverter {

	/**
	 * 默認值
	 */
	private Object defaultValue = null;
	
	/**
	 * 值轉換 map
	 */
	private Map<Object, Object> conversionMap = new HashMap<>();
	
	/**
	 * cell 值格式字串
	 */
	private String cellValueFormat = null;
	/**
	 * 取得 Cell 值的格式字串
	 * @return
	 */
	public String getFormat() {
		
		return this.cellValueFormat;
	}
	/**
	 * 設定 Cell 值格式字串
	 * @param format 格式字串
	 * @return this
	 */
	public void setFormat(String format) {
		
		this.cellValueFormat = format;
	}
	
	/**
	 * 加入轉換值
	 * @param sourceValue 原始值
	 * @param conversionValue 轉換後的值
	 * @return this
	 */
	@Override
	public void addConversion(Object sourceValue, Object conversionValue) {
		
		this.conversionMap.put(sourceValue, conversionValue);
	}
	
	/**
	 * 設定轉換的默認值
	 * @param defaultValue 默認值
	 * @return this
	 */
	public void setConversionDefaultValue(Object defaultValue) {
		
		this.defaultValue = defaultValue;
	}
	
	/**
	 * 轉換值
	 * @param columnSetting Column 設定
	 * @param cellSourceData Cell 來源數據
	 * @return 轉換後的 Cell 數據
	 */
	public Object convert(DWExcelColumnSetting columnSetting, Object cellSourceData) throws Exception {

		Object conversionValue = this.convertCellValue(columnSetting, cellSourceData);
		conversionValue = this.format(conversionValue);
		
		return conversionValue;
	}
	
	/**
	 * 轉換成 Cell 值
	 * @param columnSetting Column 設定
	 * @param cellSourceData Cell 來源數據
	 * @return
	 */
	protected Object convertCellValue(DWExcelColumnSetting columnSetting, Object cellSourceData) {
		
		Object conversionValue;
		if (conversionMap.size() > 0) {

			if (cellSourceData != null && conversionMap.containsKey(cellSourceData)) {
				
				conversionValue = conversionMap.get(cellSourceData);
			}
			else {
				
				conversionValue = defaultValue == null ? cellSourceData : defaultValue;
			}
		}
		else {
			
			conversionValue = cellSourceData;
		}
		
		return conversionValue;
	}
	
	/**
	 * 格式化單一原始值
	 * @param sourceValue
	 * @param format 格式
	 * @return 格式化後的數據
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object format(Object sourceValue) {
		
		boolean isMultipleSourceValue = sourceValue instanceof Collection;
		String format = this.getFormat();

		if (isMultipleSourceValue) {
			
			Collection<Object> sourceValues = (Collection<Object>)sourceValue;
			if (format == null) {
			
				format = StringUtils.leftPad("%s", sourceValues.size());
			}
			
			Object[] valueArray = sourceValues.stream().map(o -> o == null ? "" : o).toArray();
			return String.format(format, valueArray);
		}
		else if (format == null) {
			
			return sourceValue;
		}
		else {
		
			return String.format(format, sourceValue);
		}
	}
}
