package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Miko
 */
public class WorkOrderWorkOrderEngineerServiceTest {
    @Tested
    WorkOrderWorkOrderEngineerService workOrderWorkOrderEngineerService;

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


    @Test
    public void post() throws Exception {
        String workOrderTypeId = "mockWorkOrderTypeId";
        String workOrderEngineerName = "mockWorkOrderEngineerName";
        String workOrderEngineerId = "mockWorkOrderEngineerId";
        String email = "mockEmail";
        String startTime = "mockStartTime";
        String endTime = "";

        String tableName = "mockTableName";

        DWDataTable table = new DWDataTable();
        table.setName(tableName);

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
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("count('isPrincipal')", 0);
                        list.add(map);
                        return list;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    public DWSQLExecutionResult execute(DWDataSet dataset) throws Exception {

                        return resultInfo;
                    }
                };

                String workOrderTypeId = "mockWorkOrderTypeId";
                String workOrderEngineerName = "mockWorkOrderEngineerName";
                String workOrderEngineerId = "mockWorkOrderEngineerId";
                String email = "mockEmail";
                String startTime = "mockStartTime";
                String endTime = "mockEndTime";

                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = workOrderTypeId;

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.MAIL);
                result = email;

                dwDataRow.get(DBConstants.START_TIME);
                result = startTime;

                dwDataRow.get(DBConstants.END_TIME);
                result = endTime;

            }
        };

        DWServiceResult result = (DWServiceResult) workOrderWorkOrderEngineerService.post(new DWDataSet());
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                new MockUp<DWDaoImpl>() {
                    @Mock
                    public List<Map<String, Object>> select(IDWSQLOptions options, String statement, Object... params) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("count('isPrincipal')", 1);
                        list.add(map);
                        return list;
                    }
                };
            }
        };

        DWServiceResult result1 = (DWServiceResult) workOrderWorkOrderEngineerService.post(new DWDataSet());
        assertTrue(result1.getSuccess());

        new Expectations() {
            {
                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.post(dataset);
        } catch (Exception e) {
            assertEquals("workOrderTypeId is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = workOrderTypeId;

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.post(dataset);
        } catch (Exception e) {
            assertEquals("workOrderEngineerName is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = workOrderTypeId;

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.post(dataset);
        } catch (Exception e) {
            assertEquals("workOrderEngineerId is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = workOrderTypeId;

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.MAIL);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.post(dataset);
        } catch (Exception e) {
            assertEquals("email is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = workOrderTypeId;

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.MAIL);
                result = email;

                dwDataRow.get(DBConstants.START_TIME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.post(dataset);
        } catch (Exception e) {
            assertEquals("startTime is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.WORK_TYPE_ID);
                result = workOrderTypeId;

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.MAIL);
                result = email;

                dwDataRow.get(DBConstants.START_TIME);
                result = startTime;

                dwDataRow.get(DBConstants.END_TIME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.post(dataset);
        } catch (Exception e) {
            assertEquals("endTime is null or empty!", e.getMessage());
        }

    }

    @Test
    public void put() throws Exception {
        String workOrderEngineerName = "工程人員名稱";
        String workOrderEngineerId = "mockworkOrderEngineerId";
        String start_time = "mockStart_time";
        String end_time = "mockEnd_time";

        String tableName = "mockTableName";

        DWDataTable table = new DWDataTable();
        table.setName(tableName);

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

                        return resultInfo;
                    }
                };

                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.START_TIME);
                result = start_time;

                dwDataRow.get(DBConstants.END_TIME);
                result = end_time;
            }
        };

        DWServiceResult result = (DWServiceResult) workOrderWorkOrderEngineerService.put(new DWDataSet());
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.put(dataset);
        } catch (Exception e) {
            assertEquals("workOrderEngineerName is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.put(dataset);
        } catch (Exception e) {
            assertEquals("workOrderEngineerId is null or empty!", e.getMessage());//---
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.START_TIME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.put(dataset);
        } catch (Exception e) {
            assertEquals("start_time is null or empty!", e.getMessage());//---
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.ENGINEER_NAME);
                result = workOrderEngineerName;

                dwDataRow.get(DBConstants.ENGINEER_ID);
                result = workOrderEngineerId;

                dwDataRow.get(DBConstants.START_TIME);
                result = start_time;

                dwDataRow.get(DBConstants.END_TIME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderWorkOrderEngineerService.put(dataset);
        } catch (Exception e) {
            assertEquals("end_time is null or empty!", e.getMessage());
        }

    }

    @Test
    public void delete() throws Exception {
        String id = "mockId";
        String type_id = "mockTypeId";

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

        DWServiceResult deleteResult = (DWServiceResult) workOrderWorkOrderEngineerService.delete(id, type_id);
        assertTrue(deleteResult.getSuccess());

        try {
            workOrderWorkOrderEngineerService.delete(null, null);
        } catch (Exception e) {
            assertEquals("id is null or empty!", e.getMessage());
        }

    }

    @Test
    public void putIsPrincipal() throws Exception {
        String id = "mockId";
        String type_id = "mockTypeId";

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
                        DWDataSet d = new DWDataSet();
                        d.newTable(DBConstants.WORK_ORDER_ENGINEER).newRow().set(DBConstants.ENGINEER_ID, id);
//                        resultList.setDataSet(dataset);

                        return d;
                    }
                };

                new MockUp<DWDaoImpl>() {
                    @Mock
                    public int update(IDWSQLOptions options, String statement, Object... params) {
                        return 1;
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
                        return 9;
                    }
                };
            }
        };

        try {
            workOrderWorkOrderEngineerService.putIsPrincipal(null, type_id);
        } catch (Exception e) {
            assertEquals("id is null or empty!", e.getMessage());
        }

        try {
            workOrderWorkOrderEngineerService.putIsPrincipal(id, null);
        } catch (Exception e) {
            assertEquals("type_id is null or empty!", e.getMessage());
        }

        DWServiceResult result = (DWServiceResult) workOrderWorkOrderEngineerService.putIsPrincipal(id, type_id);
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                new MockUp<DWDataRowCollection>() {
                    @Mock
                    public int size() {
                        return 0;
                    }
                };
            }
        };

        DWServiceResult result1 = (DWServiceResult) workOrderWorkOrderEngineerService.putIsPrincipal(id, type_id);
        assertTrue(result1.getSuccess());
    }
}