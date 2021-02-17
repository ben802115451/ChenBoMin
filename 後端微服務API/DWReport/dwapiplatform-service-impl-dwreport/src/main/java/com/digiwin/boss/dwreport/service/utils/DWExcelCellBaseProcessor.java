package com.digiwin.boss.dwreport.service.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Digiwin Excel Cell 基礎處理器
 * @author falcon
 *
 */
public class DWExcelCellBaseProcessor implements DWExcelCellProcessor {

	private DWExcelCellValueConverter converter;
	private DWExcelCellDecorator decorator;

	@Override
	public void setCellValueConverter(DWExcelCellValueConverter converter) {

		if (this.converter != null && converter != null &&
				this.converter != converter && converter.getFormat() == null) {
		
			converter.setFormat(this.converter.getFormat());
		}
		
		this.converter = converter;
	}
	
	@Override
	public DWExcelCellValueConverter getCellValueConverter() {

		return this.converter;
	}

	@Override
	public void setCellDecorator(DWExcelCellDecorator decorator) {

		this.decorator = decorator;
	}

	@Override
	public DWExcelCellDecorator getCellDecorator() {
		
		return this.decorator;
	}
	
	/**
	 * 處理
	 * @param cell 數據格
	 * @param columnSetting Column 設定
	 * @param rowData Row 數據
	 */
	@Override
	public void process(Cell cell, DWExcelColumnSetting columnSetting, Map<String, Object> rowData) throws Exception {

		Object cellSourceData = this.getCellSourceData(columnSetting, rowData);	
		Object cellValue = this.setCellValue(cell, columnSetting, cellSourceData);
		
		this.setCellStyle(cell, columnSetting, cellSourceData, cellValue);
	}
	
	/**
	 * 設定 Cell 值
	 * @param cell Cell
	 * @param columnSetting Column 設定
	 * @param cellSourceData Cell 來源數據
	 * @return Cell 值
	 */
	protected Object setCellValue(Cell cell, DWExcelColumnSetting columnSetting, Object cellSourceData) throws Exception {
		
		Object cellValue = this.convertSourceValueToCellValue(columnSetting, cellSourceData, this.converter);
		
		if (cellValue != null) {
			
			cell.setCellValue(String.valueOf(cellValue));
		}
		
		return cellValue;
	}
	
	/**
	 * 設定 Cell 樣式
	 * @param cell Cell
	 * @param columnSetting Column 設定
	 * @param cellSourceData Cell 來元數據
	 * @param cellValue Cell 值
	 */
	protected void setCellStyle(Cell cell, DWExcelColumnSetting columnSetting, Object cellSourceData, Object cellValue) throws Exception {
		
		DWExcelCellDecorator decorator = this.decorator;
		if (decorator != null) {
			
			decorator.style(cell, columnSetting, cellSourceData, cellValue);
		}
	}

	/**
	 * 取得 Cell 來源的數據
	 * @param columnSetting Column 設定
	 * @param rowData Row 數據
	 * @return Cell 來源數據
	 */
	protected Object getCellSourceData(DWExcelColumnSetting columnSetting, Map<String, Object> rowData) {
		
		Object value;
		String[] dataMappingKeys = columnSetting.getDataMappingKeys();
		if (dataMappingKeys == null || dataMappingKeys.length == 0) {
			
			return null;
		}

		// 單一來源值
		if (dataMappingKeys.length == 1) {
			
			value = rowData.get(dataMappingKeys[0]);
			if (columnSetting.isIntegerType() && value instanceof Number) {
				
				value = ((Number)value).intValue();
			}
			
			return value;
		}

		// 多來源值
		List<Object> sourceValues = new ArrayList<>();
		for (String key : dataMappingKeys) {

			sourceValues.add(rowData.get(key));
		}
		
		return sourceValues;
	}
	
	/**
	 * 將原始值轉換為 cell 值
	 * @param cellSourceData Cell 來源數據
	 * @param converter 轉換器
	 * @return Cell 值
	 */
	protected Object convertSourceValueToCellValue(DWExcelColumnSetting columnSetting, Object cellSourceData, DWExcelCellValueConverter converter) throws Exception {
		
		Object cellValue;
		if (converter == null) {
		
			cellValue = cellSourceData;
		}
		else {

			cellValue = converter.convert(columnSetting, cellSourceData);
		}
		
		return cellValue;
	}
}
