package com.digiwin.dwapiplatform.dwsysmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWBusinessException;
import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.metadata.loader.DWMetadataLoaderManager;
import com.digiwin.app.module.spring.SpringContextUtils;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.juli.logging.LogFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RelesNoteServiceTest {

    @Tested
    RelesNoteService target;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dao = new DWDaoImpl(queryRunner);

    DWDataSet dataset;

    @Mocked
    DWDataSetOperationOption mockDWDataSetOperationOption;

    @Mocked
    DWDataSetBuilder mockDWDataSetBuilder;

    @Mocked
    DWMetadataContainer mockDWMetadataContainer;

    @Mocked
    DWDataColumnCollection mockDWDataColumnCollection;

    @Mocked
    SpringContextUtils mockSpringContextUtils;

    @Test
    @DisplayName("post")
    void test_post_case() throws Exception {

        new Expectations() {
            {
                {
                    dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                    result = dataset;
                }
            }
        };
        DWServiceResult postResult = (DWServiceResult) target.post(dataset);

        assertTrue(postResult.getSuccess());
        assertEquals("新增成功", postResult.geMessage());
        assertEquals(null, postResult.getData());
    }

    @Test
    @DisplayName("put")
    void test_put_case() throws Exception {

        new Expectations() {
            {
                {
                    dao.execute((DWDataSet) any);
                    result = dataset;
                }
            }
        };
        DWServiceResult putResult = (DWServiceResult) target.put(dataset);

        assertTrue(putResult.getSuccess());
        assertEquals("公告更新成功", putResult.geMessage());
        assertEquals(null, putResult.getData());
    }

    @Test
    @DisplayName("getActiveList")
    void test_getActiveList_case() throws Exception {

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("releaseid", 3);
        resultMap.put("user", "Bomin");
        resultMap.put("releaseDate", "2020/02/06 00:00:00");
        resultMap.put("status", 1);
        resultMap.put("subject", "test002adsad");
        resultMap.put("content", "測試資料dada");
        resultList.add(resultMap);

        new Expectations() {
            {
                dao.select((DWDataSetOperationOption) any, anyString, anyString);
                result = resultList;
            }
        };

        DWServiceResult getActiveListResult = (DWServiceResult) target.getActiveList();
        List<Map<String, Object>> getActiveList = (List<Map<String, Object>>) getActiveListResult.getData();
        Map<String, Object> getActiveListMap = getActiveList.get(0);

        assertEquals(3, getActiveListMap.get("releaseid"));
        assertEquals("Bomin", getActiveListMap.get("user"));
        assertEquals("2020/02/06 00:00:00", getActiveListMap.get("releaseDate"));
        assertEquals(1, getActiveListMap.get("status"));
        assertEquals("test002adsad", getActiveListMap.get("subject"));
        assertEquals("測試資料dada", getActiveListMap.get("content"));
        assertTrue(getActiveListResult.getSuccess());
    }

    @Test
    @DisplayName("getList")
    void test_getList_case() throws Exception {

        new Expectations() {
            {
                {
                    dao.selectWithPage((DWPagableQueryInfo) any);
                    result = dataset;
                }
            }
        };

        DWServiceResult getListResult = (DWServiceResult) target.getList(new DWPagableQueryInfo());

        assertTrue(getListResult.getSuccess());
        assertEquals(null, getListResult.geMessage());
        assertEquals(null, getListResult.getData());
    }

    @Test
    @DisplayName("getDetails")
    void test_getDetails_case() throws Exception {

        //情境1 : 正常取得

        List<Object> oids = new ArrayList<>();

        oids.add(1);
        oids.add(2);
        oids.add(3);

        String sql = "SELECT * from release_note WHERE 1 = 1 AND null";
        String fileSql = "SELECT * from release_note_file_attachment WHERE 1=1 AND null";
        List<Object> sqlParams = new ArrayList<Object>();

        List<Map<String, Object>> result_list = new ArrayList<>();
        Map<String, Object> result_map = new HashMap<>();
        result_map.put("releaseid", 1);
        result_map.put("user", "userTest");
        result_map.put("releaseDate", "2020-02-12");
        result_map.put("status", 1);
        result_map.put("subject", "subjectTest");
        result_map.put("content", "contentTest");
        result_list.add(result_map);

        List<Map<String, Object>> resultDetail_list = new ArrayList<>();
        Map<String, Object> resultDetail_map = new HashMap<>();
        resultDetail_map.put("fileId", "fileIdTest");
        resultDetail_map.put("releaseid", 1);
        resultDetail_map.put("fileName", "fileNameTest");
        resultDetail_map.put("displayName", "displayNameTest");
        resultDetail_map.put("url", "urlTest");
        resultDetail_list.add(resultDetail_map);

        new Expectations() {
            {
                {
                    dao.select((DWDataSetOperationOption) any, sql, sqlParams.toArray());
                    if (sql.startsWith("SELECT * from release_note")) {
                        result = result_list;
                    }
                    dao.select((DWDataSetOperationOption) any, fileSql, sqlParams.toArray());
                    if (fileSql.startsWith("SELECT * from release_note_file_attachment")) {
                        result = resultDetail_list;
                    }
                }
            }
        };

        DWServiceResult getDetailsResult = (DWServiceResult) target.getDetails(oids);
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) getDetailsResult.getData();
        Map<String, Object> dataMap = dataList.get(0);
        List<Map<String, Object>> detailsList = (List<Map<String, Object>>) dataMap.get("dtail");
        Map<String, Object> detailsMap = detailsList.get(0);

        assertTrue(getDetailsResult.getSuccess());
        assertEquals(null, getDetailsResult.geMessage());

        assertEquals(1, dataMap.get("releaseid"));
        assertEquals("userTest", dataMap.get("user"));
        assertEquals("2020-02-12", dataMap.get("releaseDate"));
        assertEquals(1, dataMap.get("status"));
        assertEquals("subjectTest", dataMap.get("subject"));
        assertEquals("contentTest", dataMap.get("content"));

        assertEquals("fileIdTest", detailsMap.get("fileId"));
        assertEquals(1, detailsMap.get("releaseid"));
        assertEquals("fileNameTest", detailsMap.get("fileName"));
        assertEquals("displayNameTest", detailsMap.get("displayName"));
        assertEquals("urlTest", detailsMap.get("url"));

        //情境2 : 公告詳情為NULL

        List<Object> nullOid = new ArrayList<>();
        DWServiceResult getDetailsResult2 = (DWServiceResult) target.getDetails(nullOid);
        List<Map<String, Object>> data = (List<Map<String, Object>>) getDetailsResult2.getData();
        assertTrue(getDetailsResult2.getSuccess());
        assertEquals(null, getDetailsResult2.geMessage());
        assertEquals(0, data.size());
    }

    @Test
    @DisplayName("delete")
    void test_delete_case() throws Exception {

        List<Object> oids = new ArrayList<>();

        new Expectations() {
            {
                {
                    new MockUp<DWDataSet>() {
                        @Mock
                        void $init() {
                        }

                        @Mock
                        void $clinit() {
                        }
                    };

                    dao.execute((DWDataSet) any, (DWDataSetOperationOption) any);
                    result = dataset;
                }
            }
        };

        DWServiceResult deleteResult = (DWServiceResult) target.delete(oids);
        assertTrue(deleteResult.getSuccess());
        assertEquals(null, deleteResult.geMessage());
        assertEquals(null, deleteResult.getData());
    }

    @Test
    @DisplayName("putActive")
    void test_putActive_case() throws Exception {

        //情境1 : 正常上架

        String oid = "1";
        DWDataSet dataset = new DWDataSet();
        DWDataRow row = dataset.newTable("release_note").newRow().set("status", 0);

        new Expectations() {
            {
                {
                    dao.selectOne((DWQueryInfo) any);
                    result = row;
                }
                {
                    dao.update(anyString, oid);
                    result = 1;
                }
            }
        };
        DWServiceResult putActiveResult = (DWServiceResult) target.putActive(oid);
        assertTrue(putActiveResult.getSuccess());
        assertEquals(null, putActiveResult.geMessage());
        assertEquals(1, putActiveResult.getData());

        //情境2 : 入參id為null

        String nullOid = new String();
        putActiveResult = new DWServiceResult();
        try {
            putActiveResult = (DWServiceResult) target.putActive(nullOid);
        } catch (DWArgumentException e) {
            assertTrue(putActiveResult.getSuccess());
            assertTrue(e.getMessage().contains("id is null or empty!"));
        }

        //情境3 : 找不到指定公告id

        new Expectations() {
            {
                {
                    dao.selectOne((DWQueryInfo) any);
                    result = null;
                }
            }
        };
        try {
            putActiveResult = (DWServiceResult) target.putActive(oid);
        } catch (DWBusinessException e) {
            assertTrue(putActiveResult.getSuccess());
            assertTrue(e.getMessage().contains("找不到指定的公告releaseid「1」"));
        }

        //情境4 : 指定公告已上架

        row.set("status", 1);

        new Expectations() {
            {
                {
                    dao.selectOne((DWQueryInfo) any);
                    result = row;
                }
            }
        };
        try {
            putActiveResult = (DWServiceResult) target.putActive(oid);
        } catch (DWBusinessException e) {
            assertTrue(putActiveResult.getSuccess());
            assertTrue(e.getMessage().contains("此公告已上架"));
        }
    }

    @Test
    @DisplayName("putInactive")
    void test_putInactive_case() throws Exception {

        //情境1 : 正常下架

        String oid = "1";
        DWDataSet dataset = new DWDataSet();
        DWDataRow row = dataset.newTable("release_note").newRow().set("status", 1);

        new Expectations() {
            {
                {
                    dao.selectOne((DWQueryInfo) any);
                    result = row;
                }
                {
                    dao.update(anyString, oid);
                    result = 1;
                }
            }
        };
        DWServiceResult putInactiveResult = (DWServiceResult) target.putInactive(oid);
        assertTrue(putInactiveResult.getSuccess());
        assertEquals(null, putInactiveResult.geMessage());
        assertEquals(1, putInactiveResult.getData());

        //情境2 : 入參id為null

        String nullOid = new String();
        putInactiveResult = new DWServiceResult();
        try {
            putInactiveResult = (DWServiceResult) target.putInactive(nullOid);
        } catch (DWArgumentException e) {
            assertTrue(putInactiveResult.getSuccess());
            assertTrue(e.getMessage().contains("id is null or empty!"));
        }

        //情境3 : 找不到指定公告id

        try {
            new Expectations() {
                {
                    {
                        dao.selectOne((DWQueryInfo) any);
                        result = null;
                    }
                }
            };
            putInactiveResult = (DWServiceResult) target.putInactive(oid);
        } catch (DWBusinessException e) {
            assertTrue(putInactiveResult.getSuccess());
            assertTrue(e.getMessage().contains("找不到指定的公告releaseid「1」"));
        }

        //情境4 : 指定公告已下架

        row.set("status", 0);

        new Expectations() {
            {
                {
                    dao.selectOne((DWQueryInfo) any);
                    result = row;
                }
            }
        };
        try {
            putInactiveResult = (DWServiceResult) target.putInactive(oid);
        } catch (DWBusinessException e) {
            assertTrue(putInactiveResult.getSuccess());
            assertTrue(e.getMessage().contains("此公告已下架"));
        }
    }
}