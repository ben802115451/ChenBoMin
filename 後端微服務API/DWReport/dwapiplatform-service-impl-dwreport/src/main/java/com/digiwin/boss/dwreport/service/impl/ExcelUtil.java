package com.digiwin.boss.dwreport.service.impl;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

/**
 * @author Miko
 */
public class ExcelUtil {

    private XSSFWorkbook workbook = new XSSFWorkbook();

    public ExcelUtil() {
        Font font = workbook.createFont();
        font.setFontName("微軟正黑");
    }

    public XSSFWorkbook getExcelFile() {
        return workbook;
    }


    public void addSheet(String sheetTitle, String[] headerArr, List<Map<String, Object>> data, OnDataConvert onDataConvert) {
        //create sheet
        XSSFSheet sheet = workbook.createSheet(sheetTitle);

        //create header title
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headerArr.length; i++) {
            headerRow.createCell(i).setCellValue(headerArr[i]);
        }

//        ResourceBundle bundle = new ResourceBundle() {
//            @Override
//            protected Object handleGetObject(String key) {
//                return null;
//            }
//
//            @Override
//            public Enumeration<String> getKeys() {
//                return null;
//            }
//        };
//        Iterator<String> key = bundle.keySet().iterator();
//        int i = 0;
//        while (key.hasNext()) {
//            String value = key.next();
//            headerRow.createCell(i).setCellValue(bundle.getString(value));
//        }

        //add Data
        for (int index = 0; index < data.size(); index++) {
            Map<String, Object> rowData = data.get(index);
            XSSFRow dataRow = sheet.createRow(index + 1);
            Object[] convertData = onDataConvert.onDataConvert(rowData);
            if (convertData.length != headerArr.length) throw new RuntimeException("欄位數量與 title 不符");

            for (int i = 0; i < convertData.length; i++) {
                Object tmp = convertData[i];
                if(tmp != null && !tmp.toString().isEmpty()){
                    tmp = tmp.toString().replaceAll("null", "");
                    dataRow.createCell(i).setCellValue(tmp.toString());
                }

                /*
                String tmp = (String) convertData[i];

                if(tmp != null){
                    tmp = tmp.replaceAll("null", "");

                dataRow.createCell(i).setCellValue((String)convertData[i]);

                 */

                //sheet.autoSizeColumn(index);
            }
            //sheet.autoSizeColumn(index);
        }

    }

    interface OnDataConvert {
        Object[] onDataConvert(Map<String, Object> rowData);
    }
}