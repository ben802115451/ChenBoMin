package com.digiwin.dwapi.dwsys.service;

import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;

import java.util.List;

/**
 * 取號服務介面定義<br>
 *
 * @author Bomin
 */
public interface IDocumentIdService extends DWService {

    /**
     * @param prefix         前綴
     * @param year           年
     * @param month          月
     * @param day            日
     * @param tableName      表格名稱
     * @param columnName     欄位名稱
     * @param serialIdLength 流水號長度
     * @return
     * @throws Exception
     */
    @AllowAnonymous
    public String getId(String prefix, int serialIdLength, int year, int month, int day, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(String prefix, int serialIdLength, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(String prefix, int year, int month, int day, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(String prefix, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(int serialIdLength, int year, int month, int day, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(int serialIdLength, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(int year, int month, int day, String tableName, String columnName) throws Exception;

    @AllowAnonymous
    public String getId(String tableName, String columnName) throws Exception;

    /**
     * 取號(多組)
     *
     * @param prefix         前綴
     * @param year           年
     * @param month          月
     * @param day            日
     * @param tableName      表格名稱
     * @param columnName     欄位名稱
     * @param number         組數
     * @param serialIdLength 流水號長度
     * @return
     * @throws Exception
     */
    public List<String> getIdList(String prefix, int serialIdLength, int year, int month, int day, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(String prefix, int serialIdLength, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(String prefix, int year, int month, int day, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(String prefix, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(int serialIdLength, int year, int month, int day, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(int serialIdLength, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(int year, int month, int day, String tableName, String columnName, int number) throws Exception;

    public List<String> getIdList(String tableName, String columnName, int number) throws Exception;
}
