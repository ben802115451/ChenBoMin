package com.digiwin.util;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.data.exceptions.DWDataException;
import com.digiwin.utils.DWTenantUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 取號入參設定<br>
 *
 * @author Bomin
 */
public class DocumentIdSetting {

    String tableName;
    String columnName;
    String prefix;
    String pattern = "yyyyMMdd";
    int serialIdLength = 4;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int number = 1;
    boolean tenantEnabled = DWTenantUtils.isTenantenabled();
    DWDao dao;

    public DocumentIdSetting(String tableName, String columnName) {
        if (tableName == null || tableName.isEmpty() || columnName == null || columnName.isEmpty()) {
            throw new DWDataException("tableName && columnName can't be null");
        }
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getPrefix() {
        return this.prefix == null ? "" : this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getSerialIdLength() {
        return this.serialIdLength;
    }

    public void setSerialIdLength(int serialIdLength) {
        this.serialIdLength = serialIdLength;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public void setDate(int year, int month, int day) throws Exception {
        StringBuilder strDate = new StringBuilder();
        strDate.append(year).append(formatMonth(month)).append(formatDay(day));

        SimpleDateFormat dFormat = new SimpleDateFormat(this.pattern);
        dFormat.setLenient(false);
        //如果成功就是正確的日期，失敗就是有錯誤的日期。
        dFormat.parse(strDate.toString());

        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isTenant() {
        return this.tenantEnabled;
    }

    public void disableTenant() {
        this.tenantEnabled = false;
    }

    public DWDao getDao() {
        return this.dao;
    }

    public void setDao(DWDao dao) {
        this.dao = dao;
    }

    /**
     * 月格式編碼<br>
     * 例:10回傳10, 9回傳09<br>
     *
     * @param month
     * @return month
     */
    private String formatMonth(int month) throws Exception {
        return String.format("%02d", month);
    }

    /**
     * 日格式編碼<br>
     * 例:10回傳10, 9回傳09<br>
     *
     * @param day
     * @return day
     */
    private String formatDay(int day) throws Exception {
        return String.format("%02d", day);
    }
}
