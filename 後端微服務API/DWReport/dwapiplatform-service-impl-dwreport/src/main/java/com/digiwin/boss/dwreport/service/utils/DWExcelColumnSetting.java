package com.digiwin.boss.dwreport.service.utils;

/**
 * Digiwin Excel Column 設定
 * @author falcon
 *
 */
public class DWExcelColumnSetting {

	/**
	 * Column 標題
	 */
	private String title;
	/**
	 * 數據對應的鍵值
	 */
	private String[] dataMappingKeys;
	/**
	 * 是否為整數型態
	 */
	private boolean isIntegerType = false;
	/**
	 * Digiwin Excel Cell 處理器
	 */
	private DWExcelCellProcessor cellProcessor;
	
	/**
	 * 設定標題
	 * @param title 標題
	 * @return this
	 */
	public DWExcelColumnSetting setTitle(String title) {
		
		this.title = title;
		
		return this;
	}
	/**
	 * 設定為整數型態
	 * @return this
	 */
	public DWExcelColumnSetting setIntegerType() {
		
		this.isIntegerType = true;
		
		return this;
	}
	/**
	 * 是否為整數型態
	 * @return 是或否
	 */
	public boolean isIntegerType() {
		
		return this.isIntegerType;
	}
	
	/**
	 * 取得標題
	 * @return
	 */
	public String getTitle() {
		
		return this.title;
	}
	
	/**
	 * 取得數據對應的鍵值
	 * @return 數據對應的鍵值
	 */
	public String[] getDataMappingKeys() {
		
		if (this.dataMappingKeys == null) {
			
			this.dataMappingKeys = new String[] {};
		}
		
		return this.dataMappingKeys;
	}
	
	/**
	 * 設定數據對應的鍵值
	 * @param key 鍵
	 * @return this
	 */
	public DWExcelColumnSetting setDataMappingKey(String key) {
		
		this.dataMappingKeys = new String[] { key };
		
		return this;
	}
	/**
	 * 設定數據對應的鍵值和組合的格式化字串
	 * @param keys 數據對應的鍵值
	 * @param format 組合的格式化字串
	 * @return this
	 */
	public DWExcelColumnSetting setDataMappingKeys(String[] keys, String format) {
		
		this.dataMappingKeys = keys;
		
		this.getCellValueConverter().setFormat(format);

		return this;
	}
	/**
	 * 加入轉換值
	 * @param sourceValue 原始值
	 * @param conversionValue 轉換後的值
	 * @return this
	 */
	public DWExcelColumnSetting addDataConvertion(Object sourceValue, Object conversionValue) {

		this.getCellValueConverter().addConversion(sourceValue, conversionValue);
		
		return this;
	}
	
	/**
	 * 加入轉換值
	 * @param defaultValue 默認值
	 * @return this
	 */
	public DWExcelColumnSetting setDataConversionDefaultValue(String defaultValue) {

		this.getCellValueConverter().setConversionDefaultValue(defaultValue);
		
		return this;
	}
	
	/**
	 * 設定 Excel Cell 處理器
	 * @param processor 處理器
	 * @return this
	 */
	public DWExcelColumnSetting setCellProcessor(DWExcelCellProcessor processor) {
		
		this.cellProcessor = processor;
		
		return this;
	}
	/**
	 * 取得 Excel Cell 處理器
	 * @return Excel Cell 處理器
	 */
	public DWExcelCellProcessor getCellProcessor() {
		
		if (this.cellProcessor == null) {
			
			this.cellProcessor = new DWExcelCellBaseProcessor();
		}
		
		return this.cellProcessor;
	}
	/**
	 * 取得 Excel Cell 值轉換器
	 * @return 轉換器
	 */
	protected DWExcelCellValueConverter getCellValueConverter() {
		
		DWExcelCellProcessor processor = this.getCellProcessor();
		if (processor.getCellValueConverter() == null) {
			
			processor.setCellValueConverter(new DWExcelCellValueBaseConverter());
		}
		
		return processor.getCellValueConverter();
	}
	
//	/**
//	 * 設定 Excel Cell 裝飾器
//	 * @param decorator 裝飾器
//	 * @return this
//	 */
//	public DWExcelColumnSetting setCellDecorator(DWExcelCellDecorator decorator) {
//		
//		this.cellDecorator = decorator;
//		
//		return this;
//	}
//	/**
//	 * 取得 Excel Cell 裝飾器
//	 * @return Cell 裝飾器
//	 */
//	public DWExcelCellDecorator getCellDecorator() {
//		
//		return this.cellDecorator;
//	}
}
