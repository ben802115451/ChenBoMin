package com.digiwin.deploy.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWQueryElement;
import com.digiwin.app.dao.DWQueryField;
import com.digiwin.app.dao.DWQueryInfo;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.app.data.DWDataTable;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.service.DWServiceResult;
import com.google.gson.Gson;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

class InstallServiceTest {
	@Tested
	InstallService installService;
	@Injectable
	DWDao dao;

	@BeforeAll
	static void beforeAll() {

		DWDeploymentEnvironment.init();
	}

	
	@Test
	@SuppressWarnings("rawtypes")
	
	void testInstallServiceGetPublishReleased() throws Exception {

		new Expectations() {
			{

				dao.select(withAny(new DWQueryInfo()), withAny(new DWDataSetOperationOption()));

				result = new Delegate() {
					@SuppressWarnings("unused")
					DWDataSet select(DWQueryInfo queryInfo) throws Exception {

						Gson gson = DWGsonProvider.getGson();
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(this.getClass().getResourceAsStream("/test-datasetGetPublishReleased.json")));
						DWDataSet dataset = gson.fromJson(bufferedReader, DWDataSet.class);

						DWQueryField qf;
						List<DWQueryElement> items = queryInfo.getCondition().getItems();
						Map<String, Object> values = new HashMap<String, Object>();
						for (DWQueryElement item : items) {

							if (item instanceof DWQueryField) {

								qf = (DWQueryField) item;
								if (DBConstants.APP_ID.equals(qf.getName())) {

									values.put(DBConstants.APP_ID, ((DWQueryField) item).getFirstValue());
								}
							}
						}

						DWDataTable table = dataset.getTable(DBConstants.DEPLOY_INFO);
						table.getRows().stream().forEach(row -> {

							if (values.containsKey(DBConstants.APP_ID)) {

								row.set(DBConstants.APP_ID, values.get(DBConstants.APP_ID));
								row.set(DBConstants.DEPLOY_AREA, values.get(DBConstants.DEPLOY_AREA));
							}
						});

						return dataset;
					}
				};
			}
		};

		Map<String, Object> param = new HashMap<String, Object>();

		// 測試場景一 - 沒有傳入 app_id、deploy_area
		Throwable exception = Assertions.assertThrows(DWArgumentException.class, () -> {
			String appId = "";
			String deployArea = "";
			param.put(DBConstants.APP_ID, appId);
			param.put(DBConstants.DEPLOY_AREA, deployArea);
			installService.getPublishReleased(appId, deployArea);
		});

		Object actaulFocusTarget = ((DWArgumentException) exception).getInstructors().get("focusTarget");
		Assertions.assertEquals(DBConstants.APP_ID, actaulFocusTarget);

		// 測試場景二
		String appId = "oee";
		String deployArea = "c";
		param.put(DBConstants.APP_ID, appId);
		param.put(DBConstants.DEPLOY_AREA, deployArea);
		Object result = installService.getPublishReleased(appId, deployArea);

		DWServiceResult serviceResult = (DWServiceResult) result;

		DWDataSet dataset = (DWDataSet) serviceResult.getData();
		DWDataTable dataTable = dataset.getTable(DBConstants.DEPLOY_INFO);
		Object[] actualAppIds = dataTable.getRows().stream().map(row -> (String) row.get(DBConstants.APP_ID)).toArray();

		Object[] expectedAppIds = new Object[actualAppIds.length];
		Arrays.fill(expectedAppIds, appId);

		Assertions.assertArrayEquals(expectedAppIds, actualAppIds);
		
	}


}
