package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.dao.*;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Miko
 */
public class WorkOrderTypeServiceTest {
    @Tested
    WorkOrderTypeService workOrderTypeService;

    @Mocked
    QueryRunner queryRunner;

    @Injectable
    DWDaoImpl dao = new DWDaoImpl(queryRunner);

    DWDataSet dataset;

    @Mocked
    DWDataSetBuilder dwDataSetBuilder;

    @Mocked
    DWMetadataContainer dwMetadataContainer;

    @Mocked
    DWDataColumnCollection dwDataColumnCollection;

    @Mocked
    DWSQLExecutionResult dwsqlExecutionResult;

    @Mocked
    DWDataSetOperationOption dwDataSetOperationOption;

    @Mocked
    DWDataRowCollection dwDataRowCollection;

    @Mocked
    DWDataRow dwDataRow;

    DWSQLExecutionResult resultInfo;

    @Mocked
    DWDataSetOperationOption option;


    @Test
    public void post() throws Exception {
        String serviceTypeId = "mockServiceTypeId";
        String workOrderTypeName = "工單類型名稱";

        DWDataTable table = new DWDataTable();
        table.setName("work-order-service");

        DWSQLExecutionResult resultInfo = new DWSQLExecutionResult();

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map profile = new HashMap<String, Object>();
                        profile.put("createByUserId", "99990000");
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

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public int update(String statement, Object... params) throws Exception {
                        return 1;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset) throws Exception {

                        return resultInfo;
                    }
                };


                dwDataRow.get(DBConstants.SERVICE_TYPE_ID);
                result = serviceTypeId;

                dwDataRow.get(DBConstants.WORK_TYPE_NAME);
                result = workOrderTypeName;
            }
        };

        DWServiceResult result = (DWServiceResult) workOrderTypeService.post(new DWDataSet());
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_TYPE_ID);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderTypeService.post(dataset);
        } catch (Exception e) {
            assertEquals("serviceTypeId is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_TYPE_ID);
                result = serviceTypeId;

                dwDataRow.get(DBConstants.WORK_TYPE_NAME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderTypeService.post(dataset);
        } catch (Exception e) {
            assertEquals("workOrderTypeName is null or empty!", e.getMessage());
        }
    }

    @Test
    public void put() throws Exception {
        String serviceTypeId = "mockServiceTypeId";
        String workOrderTypeName = "工單類型名稱";

        DWDataTable table = new DWDataTable();
        table.setName("work-order-service");

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
                    public DWSQLExecutionResult execute(DWDataSet dataset) throws Exception {

                        return dwsqlExecutionResult;
                    }
                };


                dwDataRow.get(DBConstants.SERVICE_TYPE_ID);
                result = serviceTypeId;

                dwDataRow.get(DBConstants.WORK_TYPE_NAME);
                result = workOrderTypeName;

            }
        };

        DWServiceResult result = (DWServiceResult) workOrderTypeService.put(new DWDataSet());
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_TYPE_ID);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderTypeService.put(dataset);
        } catch (Exception e) {
            assertEquals("serviceTypeId is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_TYPE_ID);
                result = serviceTypeId;

                dwDataRow.get(DBConstants.WORK_TYPE_NAME);
                result = null;

            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderTypeService.put(dataset);
        } catch (Exception e) {
            assertEquals("workOrderTypeName is null or empty!", e.getMessage());
        }
    }

    @Test
    public void delete() throws Exception {
        List<Object> oids = new ArrayList<>();
        oids.add(0000007);

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public DWSQLExecutionResult execute(DWDataSet dataset, DWDataSetOperationOption option) {
                        return dwsqlExecutionResult;
                    }
                };
            }
        };

        DWServiceResult deleteResult = (DWServiceResult) workOrderTypeService.delete(oids);
        assertTrue(deleteResult.getSuccess());

        try {
            workOrderTypeService.delete(null);
        } catch (Exception e) {
            assertEquals("id is null or empty!", e.getMessage());
        }

    }

    @Test
    public void getList() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(DBConstants.SERVICE_TYPE_ID, 00001);
        params.put(DBConstants.WORK_TYPE_ID, 0000001);
        params.put(DBConstants.ENGINEER_NAME, "engineer_name");
        params.put(DBConstants.MAIL, "digiwin@digiwin.com");

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) {
                        return new ArrayList<>();
                    }
                };
            }
        };
        DWServiceResult result = (DWServiceResult) workOrderTypeService.getList(params);
        assertTrue(result.getSuccess());
    }

    @Test
    public void getDetailList() throws Exception {
        List<Object> oids = new ArrayList<>();
        oids.add(0000007);
        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public DWDataSet select(DWQueryInfo queryInfo, DWDataSetOperationOption option) throws Exception {
                        return new DWDataSet();
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

            }
        };
        DWServiceResult result = (DWServiceResult) workOrderTypeService.getDetailList(oids);
        assertTrue(result.getSuccess());

        try {
            workOrderTypeService.getDetailList(null);
        } catch (Exception e) {
            assertEquals("id is null or empty!", e.getMessage());
        }
    }
}