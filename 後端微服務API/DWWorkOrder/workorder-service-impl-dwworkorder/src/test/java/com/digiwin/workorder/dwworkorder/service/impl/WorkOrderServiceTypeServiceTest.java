package com.digiwin.workorder.dwworkorder.service.impl;

import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.data.*;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Miko
 */
public class WorkOrderServiceTypeServiceTest {
    @Tested
    WorkOrderServiceTypeService workOrderServiceTypeService;

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

//    @Tested
//    DWDataRow dataRow;


    @Test
    public void post() throws Exception {
        String service_name = "服務名稱";

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


                dwDataRow.get(DBConstants.SERVICE_NAME);
                result = service_name;

            }
        };

        DWServiceResult result = (DWServiceResult) workOrderServiceTypeService.post(new DWDataSet());
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_NAME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderServiceTypeService.post(dataset);
        } catch (Exception e) {
            assertEquals("serviceName is null or empty!", e.getMessage());
        }
    }

    @Test
    public void put() throws Exception {
        String service_id = "mockServiceId";
        String service_name = "服務名稱";

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

                        return resultInfo;
                    }
                };


                dwDataRow.get(DBConstants.SERVICE_NAME);
                result = service_name;

                dwDataRow.get(DBConstants.SERVICE_ID);
                result = service_id;

            }
        };

        DWServiceResult result = (DWServiceResult) workOrderServiceTypeService.put(new DWDataSet());
        assertTrue(result.getSuccess());

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_NAME);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderServiceTypeService.put(dataset);
        } catch (Exception e) {
            assertEquals("serviceName is null or empty!", e.getMessage());
        }

        new Expectations() {
            {
                dwDataRow.get(DBConstants.SERVICE_NAME);
                result = service_name;

                dwDataRow.get(DBConstants.SERVICE_ID);
                result = null;
            }
        };

        try {
            DWDataSet dataset = new DWDataSet();
            workOrderServiceTypeService.put(dataset);
        } catch (Exception e) {
            assertEquals("serviceId is null or empty!", e.getMessage());
        }
    }

    @Test
    public void delete() throws Exception {
        String id = "00007";

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

        DWServiceResult deleteResult = (DWServiceResult) workOrderServiceTypeService.delete(id);
        assertTrue(deleteResult.getSuccess());

        try {
            workOrderServiceTypeService.delete(null);
        } catch (Exception e) {
            assertEquals("id is null or empty!", e.getMessage());
        }

    }

    @Test
    public void getList() throws Exception {
        DWPagableQueryInfo queryInfo = new DWPagableQueryInfo(DBConstants.WORK_ORDER_SERVICE_TYPE);
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
        DWServiceResult result = (DWServiceResult) workOrderServiceTypeService.getList(queryInfo);
        assertTrue(result.getSuccess());

    }

    @Test
    public void getDetailList() throws Exception {
        String field = "id";
        List<Object> oids = new ArrayList<>();
        oids.add(0000007);

        new Expectations() {
            {
                new MockUp<DWDataSet>() {
                    @Mock
                    void $clinit() {
                    }
                };

                new MockUp<DWDataSet>() {
                    @Mock
                    public Map<String, Object> getSourceMap() {
                        return new HashMap<>();
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

                new MockUp<DWDataTableCollection>() {
                    @Mock
                    public DWDataTable getPrimaryTable() {
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

                new MockUp<DWDataRow>() {
                    @Mock
                    public <T> T get(String columnName) {
                        if ("work_order_type".equals(columnName)) {
                            DWReferenceDataRowCollection collection = Mockito.mock(DWReferenceDataRowCollection.class);
                            List<DWDataRow> rows = new ArrayList<>();
                            rows.add(dwDataRow);
                            Mockito.when(collection.iterator()).thenReturn(rows.iterator());
                            return (T) collection;
                        }
                        Map<String, Object> data = new HashMap<>();
                        data.put(DBConstants.SERVICE_ID, 00005);
                        data.put(DBConstants.WORK_TYPE_ID, "0000005");
                        data.put(DBConstants.WORK_ORDER_TYPE, 0000005);
                        return (T) data.get("type_id");
                    }
                };
            }
        };
        DWServiceResult result = (DWServiceResult) workOrderServiceTypeService.getDetailList(field, oids);
        assertTrue(result.getSuccess());

        try {
            workOrderServiceTypeService.getDetailList(field, null);
        } catch (Exception e) {
            assertEquals(null, e.getMessage());
        }
    }
}