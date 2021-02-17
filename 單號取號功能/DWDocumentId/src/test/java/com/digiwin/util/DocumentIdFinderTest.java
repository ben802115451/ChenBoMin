package com.digiwin.util;

import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWPaginationQueryResult;
import com.digiwin.app.data.DWDataColumnCollection;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.module.spring.SpringContextUtils;
import mockit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentIdFinderTest {

    @Tested
    DocumentIdFinder target;

    @Mocked
    DWMetadataContainer mockDWMetadataContainer;

    @Mocked
    DWDataColumnCollection mockDWDataColumnCollection;

    @Mocked
    SpringContextUtils mockSpringContextUtils;

    @Mocked
    DWDao dao;

    @Test
    void getMaxSerialNo() throws Exception {

        //場景1 : targetDao != null

        String tableName = "sys_document_test";
        String columnName = "DOCUMENT_ID";

        DocumentIdSetting setting = new DocumentIdSetting(tableName, columnName);

        DWDataSet dataSet = new DWDataSet();
        dataSet.newTable(tableName).newRow().set(columnName, "TEST202001010001");
        DWPaginationQueryResult paginationQueryResult = new DWPaginationQueryResult(1, 1);
        paginationQueryResult.setDataSet(dataSet);

        new Expectations() {
            {
                {

                    mockSpringContextUtils.getBean("dw-dao");
                    result = dao;

                    dao.selectWithPage((DWPagableQueryInfo) any);
                    result = paginationQueryResult;
                }
            }
        };

        String result = (String) target.getMaxSerialNo("TEST20200101", setting);
        assertEquals("TEST202001010001", result);

        //場景2 : targetDao == null

        new Expectations() {
            {
                {

                    mockSpringContextUtils.getBean("dw-dao");
                    result = null;
                }
            }
        };
        Assertions.assertThrows(DWException.class, () -> target.getMaxSerialNo("TEST20200101", setting));

        //場景3 : getBean  throws Exception

        new Expectations() {
            {
                {
                    new MockUp<SpringContextUtils>() {
                        @Mock
                        <T> T getBean(String name) throws Exception {
                            throw new Exception();
                        }
                    };
                }
            }
        };
        Assertions.assertThrows(DWException.class, () -> target.getMaxSerialNo("TEST20200101", setting));
    }
}