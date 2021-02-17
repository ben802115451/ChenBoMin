package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.dao.DWSqlInfo;
import com.digiwin.app.dao.dialect.DWSQLDialect;
import com.digiwin.app.data.*;
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
public class ScenarioServiceTest {
    @Tested
    ScenarioService scenarioService;

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

    @Test
    public void post() throws Exception {
        String areaType = "A";

        DWDataTable table = new DWDataTable();
        table.setName("layout_arrangement");

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map profile = new HashMap<String, Object>();
                        profile.put("lastModifyUserId", "99990000");
                        profile.put("lastModifyUserName", "99990000");
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
                    public int update(IDWSQLOptions options, String statement, Object... params) throws Exception {
                        return 1;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) throws Exception {

                        return resultInfo;
                    }
                };

                dwDataRow.get(DBConstants.AREA_TYPE);
                result = areaType;

            }
        };

        scenarioService.post(new DWDataSet());

        new Expectations() {
            {

                dwDataRow.get(DBConstants.AREA_TYPE);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            scenarioService.post(dataset);
        } catch (Exception e) {
            assertEquals("areaType is null or empty!", e.getMessage());
        }

    }

    @Test
    public void get() throws Exception {
        String areaType = "A";

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

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public int size() {
                        return 1;
                    }
                };

            }
        };

        scenarioService.get(areaType);
    }


    @Test
    public void put() throws Exception {
        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map profile = new HashMap<String, Object>();
                        profile.put("lastModifyUserId", "99990000");
                        profile.put("lastModifyUserName", "99990000");
                        return profile;
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

                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public int size() {
                        return 1;
                    }
                };
            }
        };
        DWDataSet dataSet = new DWDataSet();
//        dataSet.getTable()
        scenarioService.put(new DWDataSet());
    }
}