package com.digiwin.dwapiplatform.dwsysmanagement.service.impl;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetBuilder;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BannerServiceTest {

    @Tested
    BannerService target;

    @Injectable
    DWDao dao;

    DWDataSet dataset;

    @Mocked
    DWDataSetOperationOption mockDWDataSetOperationOption;

    @Mocked
    DWDataSetBuilder mockDWDataSetBuilder;

    @Mocked
    DWModuleMessageResourceBundleUtils dwModuleMessageResourceBundleUtils;

    @Test
    @DisplayName("post")
    void post() throws Exception {

        new Expectations() {
            {

                dao.execute((DWDataSet) any);
                result = dataset;

            }
        };
        DWServiceResult postResult = (DWServiceResult) target.post(dataset);
        assertTrue(postResult.getSuccess());
    }

    @Test
    @DisplayName("put")
    void put() throws Exception {

        new Expectations() {
            {

                dao.execute((DWDataSet) any);
                result = dataset;

            }
        };
        DWServiceResult putResult = (DWServiceResult) target.put(dataset);
        assertTrue(putResult.getSuccess());
    }

    @Test
    @DisplayName("delete")
    void delete() throws Exception {

        new Expectations() {
            {

                dao.execute((DWDataSet) any);
                result = dataset;

            }
        };
        DWServiceResult deleteResult = (DWServiceResult) target.delete(dataset);
        assertTrue(deleteResult.getSuccess());

    }

    @Test
    @DisplayName("getList")
    void getList() throws Exception {

        new Expectations() {
            {

                dao.selectWithPage((DWPagableQueryInfo) any);
                result = dataset;

            }
        };

        DWServiceResult getListResult = (DWServiceResult) target.getList(new DWPagableQueryInfo());
        assertTrue(getListResult.getSuccess());
    }

    @Test
    @DisplayName("getBlocks")
    void getBlocks() throws Exception {

        new Expectations() {
            {
                dao.select((DWPagableQueryInfo) any);
                result = dataset;

            }
        };

        DWServiceResult getBlocksResult = (DWServiceResult) target.getBlocks(new DWPagableQueryInfo());
        assertTrue(getBlocksResult.getSuccess());
    }

    @Test
    @DisplayName("postBlocks")
    void postBlocks() throws Exception {

        new Expectations() {
            {

                dao.execute((DWDataSet) any);
                result = dataset;

            }
        };
        DWServiceResult postBlocksResult = (DWServiceResult) target.postBlocks(dataset);
        assertTrue(postBlocksResult.getSuccess());
    }

    @Test
    @DisplayName("putBlocks")
    void putBlocks() throws Exception {

        new Expectations() {
            {

                dao.execute((DWDataSet) any);
                result = dataset;

            }
        };
        DWServiceResult putBlocksResult = (DWServiceResult) target.putBlocks(dataset);
        assertTrue(putBlocksResult.getSuccess());
    }

    @Test
    @DisplayName("deleteBlocks")
    void deleteBlocks() throws Exception {

        List<Object> oids = new ArrayList<>();

        new Expectations() {
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
        };

        DWServiceResult deleteBlocksResult = (DWServiceResult) target.deleteBlocks(oids);
        assertTrue(deleteBlocksResult.getSuccess());
    }

    @Test
    @DisplayName("getRotateList")
    void getRotateList() throws Exception {

        new Expectations() {
            {
                dao.select((DWPagableQueryInfo) any);
                result = dataset;
            }
        };

        DWServiceResult getRotateListResult = (DWServiceResult) target.getRotateList(new DWPagableQueryInfo());
        assertTrue(getRotateListResult.getSuccess());
    }
}