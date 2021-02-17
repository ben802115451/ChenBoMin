package com.digiwin.dwapi.dwsys.service.impl;

import com.digiwin.dwapi.dwsys.service.IDocumentIdService;
import com.digiwin.util.DocumentIdGenerator;
import com.digiwin.util.DocumentIdSetting;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 取號服務實作<br>
 *
 * @author Bomin
 */
public class DocumentIdService implements IDocumentIdService {

    @Autowired
    private DocumentIdGenerator documentIdGenerator;

    @Override
    public String getId(String prefix, int serialIdLength, int year, int month, int day, String tableName, String columnName) throws Exception {

        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setSerialIdLength(serialIdLength);
        setting.setDate(year, month, day);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(String prefix, int serialIdLength, String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setSerialIdLength(serialIdLength);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(String prefix, int year, int month, int day, String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setDate(year, month, day);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(String prefix, String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(int serialIdLength, int year, int month, int day, String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setSerialIdLength(serialIdLength);
        setting.setDate(year, month, day);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(int serialIdLength, String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setSerialIdLength(serialIdLength);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(int year, int month, int day, String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setDate(year, month, day);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public String getId(String tableName, String columnName) throws Exception {
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        String result = documentIdGenerator.getId(setting);
        return result;
    }

    @Override
    public List<String> getIdList(String prefix, int serialIdLength, int year, int month, int day, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setSerialIdLength(serialIdLength);
        setting.setDate(year, month, day);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(String prefix, int serialIdLength, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setSerialIdLength(serialIdLength);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(String prefix, int year, int month, int day, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setDate(year, month, day);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(String prefix, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setPrefix(prefix);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(int serialIdLength, int year, int month, int day, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setSerialIdLength(serialIdLength);
        setting.setDate(year, month, day);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(int serialIdLength, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setSerialIdLength(serialIdLength);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(int year, int month, int day, String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setDate(year, month, day);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }

    @Override
    public List<String> getIdList(String tableName, String columnName, int number) throws Exception {

        List<String> result = null;
        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);
        setting.setNumber(number);
        result = documentIdGenerator.getIdList(setting);
        return result;
    }
}
