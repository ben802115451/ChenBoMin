package com.digiwin.boss.dwreport.service.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DWExcelBuilder {

	/**
	 * sheet 設定清單
	 */
	private List<DWExcelSheetSetting> sheets = new ArrayList<>();
	
	/**
	 * 加入 sheet 設定
	 * @param sheetName sheet 名稱
	 * @return sheet 設定
	 */
	public DWExcelSheetSetting addSheet(String sheetName) {
	
		DWExcelSheetSetting sheet = new DWExcelSheetSetting();
		sheet.setSheetName(sheetName);

		this.sheets.add(sheet);

		return sheet;
	}
	/**
	 * 取得 sheet 設定
	 * @return  sheet 設定
	 */
	public List<DWExcelSheetSetting> getSheets() {
		
		return this.sheets;
	}
    
    /**
     * 建置
     */
    protected Workbook build() throws Exception {
    	
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	for (DWExcelSheetSetting sheet : this.getSheets()) {

    		this.buildSheet(workbook, sheet);
    	}
    	
    	return workbook;
    }

    /**
     * 建置 sheet
     * @param workbook Excel Workbook
     * @param sheetSetting Sheet 設定
     */
    protected void buildSheet(XSSFWorkbook workbook, DWExcelSheetSetting sheetSetting) throws Exception {
    	
    	XSSFSheet sheet = workbook.createSheet(sheetSetting.getSheetName());

    	this.buildTitleRow(sheet, sheetSetting);
    	int rowIndex = 1;
    	List<Map<String, Object>> datas = sheetSetting.getDatas();
    	for (Map<String, Object> data : datas) {
    		this.buildDataRow(sheet, sheetSetting, data, rowIndex++);
		}
	}
    /**
     * 建置標題 Row
     * @param sheet Sheet
     * @param sheetSetting Sheet 設定
     */
    protected void buildTitleRow(XSSFSheet sheet, DWExcelSheetSetting sheetSetting) {

    	int columnIndex = 0;
    	XSSFRow titleRow = sheet.createRow(0);

		for (DWExcelColumnSetting column : sheetSetting.getColumns()) {
    		titleRow.createCell(columnIndex++).setCellValue(column.getTitle());
			sheet.autoSizeColumn(columnIndex);
		}
    }
    /**
     * 建置數據 Row
     * @param sheet Sheet
     * @param sheetSetting Sheet 設定
     * @param data 數據
     * @param rowIndex Row 索引值
     */
    protected void buildDataRow(XSSFSheet sheet, DWExcelSheetSetting sheetSetting, Map<String, Object> data, int rowIndex) throws Exception {
    	
    	Cell cell;
    	int columnIndex = 0;
    	XSSFRow dataRow = sheet.createRow(rowIndex);
    	for (DWExcelColumnSetting columnSetting : sheetSetting.getColumns()) {
    		
    		cell = dataRow.createCell(columnIndex++);
    		columnSetting.getCellProcessor().process(cell, columnSetting, data);
		}
    }
    /**
     * 建立 Workbook
     * @return Workbook
     */
    public Workbook create() throws Exception {
 
    	Workbook workbook = this.build();

        return workbook;
    }
}
