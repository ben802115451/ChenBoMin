package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.dao.DWSqlInfo;
import com.digiwin.app.dao.dialect.DWSQLDialect;
import com.digiwin.app.data.*;
import com.digiwin.app.data.exceptions.DWDataTableNotFoundException;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.metadata.loader.DWMetadataLoaderManager;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.app.service.DWServiceContext;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Miko
 */
public class CloudThemeServiceTest {
    @Tested
    CloudThemeService cloudThemeService;

    @Mocked
    DWServiceContext dwServiceContext;

    @Mocked
    LogFactory logFactory;

    @Mocked
    DWModuleConfigUtils dwModuleConfigUtils;

    @Mocked
    DWModuleMessageResourceBundleUtils dWModuleMessageResourceBundleUtils;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dwDaoImpl = new DWDaoImpl(queryRunner);

    @Mocked
    DWSQLDialect dwsqlDialect;

    @Mocked
    DWSqlInfo dwSqlInfo;

    @Mocked
    DWDataRowCollection dwDataRowCollection;

    @Mocked
    DWDataRow dwDataRow;

    @Mocked
    DWMetadataLoaderManager mockDWMetadataLoaderManager;

    @Mocked
    DWMetadataContainer mockDWMetadataContainer;

    @Mocked
    DWDataTableBuilder mockDWDataTableBuilder;

    @Test
    public void post11() {
        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    protected DWDataTable getTable(String name, boolean throwExceptionIfNotFound) {
                        throw new DWDataTableNotFoundException(name);
//                        return null;
                    }
                };
            }
        };
        try {
            DWDataSet dataset = new DWDataSet();
            cloudThemeService.post(dataset);
        } catch (Exception e) {
            assertEquals(null, e.getMessage());
        }
    }

    @Test
    public void post() {
        String themeName = "企業應用";

        DWDataTable table = new DWDataTable();
        table.setName("theme");

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map profile = new HashMap<String, Object>();
                        profile.put("createUserId", "99990000");
                        profile.put("createUserName", "99990000");
                        return profile;
                    }

                };

                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public int update(DWDataSetOperationOption option, String statement, Object... params) throws Exception {
                        return 1;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) throws Exception {

                        return resultInfo;
                    }
                };


                dwDataRow.get(DBConstants.THEME_NAME);
                result = themeName;

            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            cloudThemeService.post(dataset);
        } catch (Exception e) {
            assertEquals(true, true);
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.THEME_NAME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            cloudThemeService.post(dataset);
        } catch (Exception e) {
            assertEquals("themeName is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    protected DWDataTable getTable(String name, boolean throwExceptionIfNotFound) {
                        throw new DWDataTableNotFoundException(name);
//                        return null;
                    }
                };
                dwDataRow.get(DBConstants.THEME_NAME);
                result = themeName;
            }
        };
        try {
            DWDataSet dataset = new DWDataSet();
            cloudThemeService.post(dataset);
        } catch (Exception e) {
            assertEquals(null, e.getMessage());
        }
    }

    @Test
    public void put() throws Exception {
        String id = "10";
        String themeName = "企業應用";

        DWDataTable table = new DWDataTable();
        table.setName("theme");

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) throws Exception {

                        return resultInfo;
                    }
                };


//                dwDataRow.get(DBConstants.ID);
//                result = id;

                dwDataRow.get(DBConstants.THEME_NAME);
                result = themeName;

            }
        };

        DWDataSet dataset = new DWDataSet();
        cloudThemeService.put(dataset);

//        new Expectations() {
//            {
//                dwDataRow.get(DBConstants.ID);
//                result = null;
//            }
//        };
//
//        try {
//            cloudThemeService.put(new DWDataSet());
//        } catch (Exception e) {
//            assertEquals("id is null or empty!", e.getMessage());
//        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.THEME_NAME);
                result = null;
            }
        };

        try {
            cloudThemeService.put(new DWDataSet());
        } catch (Exception e) {
            assertEquals("themeName is null or empty!", e.getMessage());
        }

        new Expectations() {
            {

//                new MockUp<DWDataException>() {
//                    @Mock
//                    public DWDataTableNotFoundException() {
//
//                    }
////                            throws DWDataTableNotFoundException {
////                        return null;
//                    }
//                };
            }
        };
    }

    @Test
    public void delete() throws Exception {
        List<Object> oids = new ArrayList<>();
        oids.add(1);

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();
        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) throws Exception {

                        return resultInfo;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", 0);
                        list.add(map);
                        return list;
                    }
                };

            }
        };

        cloudThemeService.delete(oids);

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", 1);
                        list.add(map);
                        return list;
                    }
                };
            }
        };

        try {
            cloudThemeService.delete(null);
        } catch (Exception e) {
            assertEquals("oid is null or empty!", e.getMessage());
        }


        try {
            cloudThemeService.delete(oids);
        } catch (Exception e) {
            assertEquals("此主題上架中，無法刪除", e.getMessage());
        }

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        return list;
                    }
                };
            }
        };

        try {
            cloudThemeService.delete(oids);
        } catch (Exception e) {
            assertEquals("找不到指定的主題 oid = " + oids, e.getMessage());
        }
    }

    @Test
    public void getActiveList() {
        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        return list;
                    }
                };

            }
        };

        try {
            cloudThemeService.getActiveList();
        } catch (Exception e) {
            assertEquals(true, true);
        }
    }

    @Test
    public void getInactiveList() {
        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        return list;
                    }
                };

            }
        };

        try {
            cloudThemeService.getInactiveList();
        } catch (Exception e) {
            assertEquals(true, true);
        }
    }

    @Test
    public void putActive() throws Exception {
        String oid = "mockId";

        DWDataTable table = new DWDataTable();
        table.setName("theme");

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) throws Exception {

                        return resultInfo;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("COUNT(STATUS)", 3);
                        map.put("status", 0);
                        list.add(map);
                        return list;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public int update(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        return 1;
                    }
                };
            }
        };

        cloudThemeService.putActive(oid);

        try {
            cloudThemeService.putActive(null);
        } catch (Exception e) {
            assertEquals("oid is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("COUNT(STATUS)", 3);
                        map.put("status", 1);
                        list.add(map);
                        return list;
                    }
                };
            }
        };

        try {
            cloudThemeService.putActive(oid);
        } catch (Exception e) {
            assertEquals("此主題已上架", e.getMessage());
        }

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("COUNT(STATUS)", 10);
                        map.put("status", 0);
                        list.add(map);
                        list.add(map);
                        list.add(map);
                        list.add(map);
                        list.add(map);
                        return list;
                    }
                };
            }
        };

        try {
            cloudThemeService.putActive(oid);
        } catch (Exception e) {
            assertEquals("activeCount cannot over 5!", e.getMessage());
        }
        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("1", 1);
                        list.add(map);
                        return list;

                    }
                };
            }
        };

        try {
            cloudThemeService.putActive(oid);
        } catch (Exception e) {
            assertEquals(null, e.getMessage());
        }

    }

    @Test
    public void putInactive() throws Exception {
        String oid = "mockId";

        DWDataTable table = new DWDataTable();
        table.setName("theme");

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) throws Exception {

                        return resultInfo;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", 1);
                        list.add(map);
                        return list;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public int update(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        return 1;
                    }
                };
            }
        };

        cloudThemeService.putInactive(oid);

        try {
            cloudThemeService.putInactive(null);
        } catch (Exception e) {
            assertEquals("oid is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", 0);
                        list.add(map);
                        return list;
                    }
                };
            }
        };

        try {
            cloudThemeService.putInactive(oid);
        } catch (Exception e) {
            assertEquals("此主題已下架", e.getMessage());
        }

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        return list;
                    }
                };
            }
        };

        try {
            cloudThemeService.putInactive(oid);
        } catch (Exception e) {
            assertEquals("找不到指定的主題 oid = " + oid, e.getMessage());
        }
    }

    @Test
    public void postGoods() throws Exception {
        String themeId = "1";
        String goodsCode = "DOP";

        new Expectations(){
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", 8);
                        map.put("themeId", 1);
                        map.put("goodsType", "app");
                        map.put("goodsCode", "DOP");
                        map.put("goodsNAme", "营运监控");
                        list.add(map);
                        return list;

                    }
                };

                dwDataRow.get(DBConstants.THEME_ID);
                result = themeId;

                dwDataRow.get(DBConstants.GOODS_CODE);
                result = goodsCode;
            }
        };

        // 場景一： themeId和goodsCode都有傳
        cloudThemeService.postGoods(new DWDataSet());

        // 場景二： themeId為null和goodsCode有傳
        new Expectations(){
            {
                dwDataRow.get(DBConstants.THEME_ID);
                result = null;

                dwDataRow.get(DBConstants.GOODS_CODE);
                result = goodsCode;
            }
        };

        try {
            cloudThemeService.postGoods(new DWDataSet());
        } catch (Exception e) {
            assertEquals("themeId is null or empty!", e.getMessage());
        }

        // 場景三： themeId有傳和goodsCode為null
        new Expectations(){
            {
                dwDataRow.get(DBConstants.THEME_ID);
                result = themeId;

                dwDataRow.get(DBConstants.GOODS_CODE);
                result = null;
            }
        };

        try {
            cloudThemeService.postGoods(new DWDataSet());
        } catch (Exception e) {
            assertEquals("goodsCode is null or empty!", e.getMessage());
        }

        // 場景四：查無此主題
        new Expectations(){
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        return list;

                    }
                };

                dwDataRow.get(DBConstants.THEME_ID);
                result = themeId;

                dwDataRow.get(DBConstants.GOODS_CODE);
                result = goodsCode;
            }
        };

        try {
            cloudThemeService.postGoods(new DWDataSet());
        } catch (Exception e) {
            assertEquals("查無此主題 themeId = " + themeId, e.getMessage());
        }
    }


    @Test
    public void getGoodsList2() throws Exception {
        String oid = "mockOid";
        String goodsCode = "B2BK";
        String displayName = "B2BK";
        String goodsName = "app";
        String categoryId = "app";
        String goodsType = "app";

        DWDataTable table = new DWDataTable();
        table.setName("goods_detail");

        new Expectations(){
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public DWDataTable getTable(String name) {
                        return new DWDataTable();
                    }
                };

                new MockUp<DWDataTable>() {

                    @Mock
                    public DWDataRowCollection getRows() {
                        return dwDataRowCollection;
                    }
                };

                new MockUp<DWDataTable>() {
                    @Mock
                    public DWDataSet getDataSet() {
                        DWDataSet dataSet = new DWDataSet();
                        return dataSet;
                    }

                };

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public Iterator<DWDataRow> iterator() {
                        List<DWDataRow> rows = new ArrayList<>();
                        rows.add(dwDataRow);
                        return rows.iterator();
                    }
                };

                new MockUp<GoodsTypeService>() {
                    @Mock
                    public Object get(List<Object> oids) throws Exception {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("code", goodsCode);
                        list.add(map);
                        return list;
                    }
                };

//                new MockUp<DWDaoImpl>() {
//                    @Mock
//                    public DWDataSet select(DWQueryInfo queryInfo) throws Exception {
//
//                        return table.getDataSet();
//                    }
//                };

//                dwDataRow.get(DBConstants.GOODS_CODE);
//                result = goodsCode;

            }
        };

        cloudThemeService.getGoodsList(oid);

    }




//    @Test
//    public void getGoodsList() throws Exception {
//        String oid = "mockOid";
//        new Expectations() {
//            {
//                new MockUp<DWDataSet>() {
//                    @Mock
//                    void $clinit() {
//                    }
//                };
//
//                new MockUp<DWDataSet>() {
//                    @Mock
//                    public DWDataTable getTable(String name) {
//                        return new DWDataTable();
//                    }
//                };
//
//                new MockUp<DWDataTable>() {
//
//                    @Mock
//                    public DWDataRowCollection getRows() {
//                        return dwDataRowCollection;
//                    }
//                };
//
//                new MockUp<DWDataRowCollection>() {
//                    @Mock
//                    public Iterator<DWDataRow> iterator() {
//                        List<DWDataRow> rows = new ArrayList<>();
//                        rows.add(dwDataRow);
//                        return rows.iterator();
//                    }
//                };
//                new MockUp<DWDaoImpl>() {
//                    @Mock
//                    public List<Map<String, Object>> select(String statement, Object... params) throws Exception {
//                        List<Map<String, Object>> list = new ArrayList<>();
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("id", 8);
//                        map.put("themeId", 1);
//                        map.put("goodsType", "app");
//                        map.put("goodsCode", "DOP");
//                        map.put("goodsNAme", "营运监控");
//                        list.add(map);
//                        return list;
//
//                    }
//                };
//            }
//        };
//
//        cloudThemeService.getGoodsList(oid);
//
//        try {
//            cloudThemeService.getGoodsList(oid);
//        } catch (Exception e) {
//            assertEquals(true, e.getMessage());
//        }
//    }
}