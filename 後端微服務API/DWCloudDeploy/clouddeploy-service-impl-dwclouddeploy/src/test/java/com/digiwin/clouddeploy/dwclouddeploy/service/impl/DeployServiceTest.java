package com.digiwin.clouddeploy.dwclouddeploy.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.resource.DWModuleMessageResourceBundleUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import mockit.*;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DeployServiceTest {

    @Tested
    DeployService target;

    @Mocked
    HttpResponse mockHttpResponse;

    @Mocked
    StatusLine mockStatusLine;

    @Mocked
    CloseableHttpClient mockCloseableHttpClient;

    @Mocked
    CloseableHttpResponse mockCloseableHttpResponse;

    @Mocked
    LogFactory mockLogFactory;

    @Test
    @DisplayName("getProductVersion")
    void test_getProductVersion_case() throws Exception {

        //場景1 : StatusCode=200

        Map<String, Object> orderContent = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        response.put("data", data);
        orderContent.put("response", response);
        String appId = "oee";

        new Expectations() {
            {
                HttpEntity entity = new StringEntity(orderContent.toString(), "UTF-8");
                mockHttpResponse.getEntity();
                result = entity;

                mockHttpResponse.getStatusLine();
                result = mockStatusLine;

                mockStatusLine.getStatusCode();
                result = 200;
            }
        };
        DWServiceResult getProductVersionResult = (DWServiceResult) target.getProductVersion(appId);
        List<Map<String, Object>> resultData = (List<Map<String, Object>>) getProductVersionResult.getData();
        assertTrue(getProductVersionResult.getSuccess());
        Assertions.assertEquals(null, getProductVersionResult.geMessage());
        Assertions.assertEquals(0, resultData.size());

        //場景2 : StatusCode=400
        new Expectations() {
            {
                mockStatusLine.getStatusCode();
                result = 400;
            }
        };
        try {
            getProductVersionResult = (DWServiceResult) target.getProductVersion(appId);
        } catch (DWException e) {
            assertTrue(getProductVersionResult.getSuccess());
            assertTrue(e.getMessage().equals("Get Product Version failed, status code = 400, please check the log for more information."));
        }
    }

    @Test
    @DisplayName("getSpecificVersion")
    void test_getSpecificVersion_case() throws Exception {
        String imageName = "oeebackend";
        String maxfullImage = "oeebackend-2.1.0:1.1.1.1000.10163";
        String minfullImage = "oeebackend-2.1.0:1.1.1.1000.10160";
        String orderBy = "DESC";

        //場景1 : StatusCode=200

        Map<String, Object> orderContent = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        response.put("data", data);
        orderContent.put("response", response);

        new Expectations() {
            {
                HttpEntity entity = new StringEntity(orderContent.toString(), "UTF-8");
                mockHttpResponse.getEntity();
                result = entity;

                mockHttpResponse.getStatusLine();
                result = mockStatusLine;

                mockStatusLine.getStatusCode();
                result = 200;
            }
        };
        DWServiceResult getSpecificVersionResult = (DWServiceResult) target.getSpecificVersion(imageName, maxfullImage, minfullImage, orderBy);

        Map<String, Object> resultMap = (Map<String, Object>) getSpecificVersionResult.getData();
        List<Map<String, Object>> resultData = (List<Map<String, Object>>) resultMap.get("data");

        assertTrue(getSpecificVersionResult.getSuccess());
        Assertions.assertEquals(null, getSpecificVersionResult.geMessage());
        Assertions.assertEquals(0, resultData.size());

        //場景2 : StatusCode=400

        new Expectations() {
            {
                mockStatusLine.getStatusCode();
                result = 400;
            }
        };
        try {
            getSpecificVersionResult = (DWServiceResult) target.getSpecificVersion(imageName, maxfullImage, minfullImage, orderBy);
        } catch (DWException e) {
            assertTrue(getSpecificVersionResult.getSuccess());
            assertTrue(e.getMessage().equals("Get Specific Version failed, status code = 400, please check the log for more information."));
        }
    }

    @Test
    @DisplayName("getLog")
    void test_getLog_case() throws Exception {
        String imageName = "oeebackend";
        String cloud = "Azure";
        String area = "test";
        String appId = "oee";
        String action = "1";
        String id = new String();

        //場景1 : StatusCode=200

        Map<String, Object> orderContent = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        response.put("data", data);
        orderContent.put("response", response);

        new Expectations() {
            {
                HttpEntity entity = new StringEntity(orderContent.toString(), "UTF-8");
                mockHttpResponse.getEntity();
                result = entity;

                mockHttpResponse.getStatusLine();
                result = mockStatusLine;

                mockStatusLine.getStatusCode();
                result = 200;
            }
        };
        DWServiceResult getLogResult = (DWServiceResult) target.getLog(imageName, cloud, area, appId, action, id);
        Map<String, Object> resultMap = (Map<String, Object>) getLogResult.getData();
        List<Map<String, Object>> resultData = (List<Map<String, Object>>) resultMap.get("data");

        assertTrue(getLogResult.getSuccess());
        Assertions.assertEquals(null, getLogResult.geMessage());
        Assertions.assertEquals(0, resultData.size());

        //場景2 : StatusCode=400

        new Expectations() {
            {
                mockStatusLine.getStatusCode();
                result = 400;
            }
        };
        try {
            getLogResult = (DWServiceResult) target.getLog(imageName, cloud, area, appId, action, id);
        } catch (DWException e) {
            assertTrue(getLogResult.getSuccess());
            assertTrue(e.getMessage().equals("Get Log failed, status code = 400, please check the log for more information."));
        }
    }

    @Test
    @DisplayName("getLogView")
    void test_getLogView_case() throws Exception {
        String imageName = "oeebackend";
        String cloud = "Azure";
        String area = "test";
        String appId = "oee";
        String action = "1";

        //場景1 : StatusCode=200

        Map<String, Object> orderContent = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        response.put("data", data);
        orderContent.put("response", response);

        new Expectations() {
            {
                HttpEntity entity = new StringEntity(orderContent.toString(), "UTF-8");
                mockHttpResponse.getEntity();
                result = entity;

                mockHttpResponse.getStatusLine();
                result = mockStatusLine;

                mockStatusLine.getStatusCode();
                result = 200;
            }
        };
        DWServiceResult getLogViewResult = (DWServiceResult) target.getLogView(imageName, cloud, area, appId, action);
        Map<String, Object> resultMap = (Map<String, Object>) getLogViewResult.getData();
        List<Map<String, Object>> resultData = (List<Map<String, Object>>) resultMap.get("data");

        assertTrue(getLogViewResult.getSuccess());
        Assertions.assertEquals(null, getLogViewResult.geMessage());
        Assertions.assertEquals(0, resultData.size());

        //場景2 : StatusCode=400

        new Expectations() {
            {
                mockStatusLine.getStatusCode();
                result = 400;
            }
        };
        try {
            getLogViewResult = (DWServiceResult) target.getLogView(imageName, cloud, area, appId, action);
        } catch (DWException e) {
            assertTrue(getLogViewResult.getSuccess());
            assertTrue(e.getMessage().equals("Get Log failed, status code = 400, please check the log for more information. -> {response={data=[]}}"));
        }
    }

    @Test
    @DisplayName("postDeployment")
    void test_postDeployment_case() throws Exception {
        String cloud = "Azure";
        String area = "test";
        String appId = "oee";
        String fullImage = "oeebackend-2.1.0:1.1.1.1000.10163";
        String userId = "ben802115451";
        String userName = "Bomin";

        //場景1 : StatusCode=200

        Map<String, Object> orderContent = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        response.put("data", data);
        orderContent.put("response", response);

        new Expectations() {
            {
                new MockUp<DWServiceContext>() {
                    @Mock
                    public Map<String, Object> getProfile() {
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put("userId", userId);
                        result.put("userName", userName);
                        return result;
                    }
                };

                HttpEntity entity = new StringEntity(orderContent.toString(), "UTF-8");
                mockHttpResponse.getEntity();
                result = entity;

                mockHttpResponse.getStatusLine();
                result = mockStatusLine;

                mockStatusLine.getStatusCode();
                result = 200;
            }
        };
        DWServiceResult postDeploymentResult = (DWServiceResult) target.postDeployment(cloud, area, appId, fullImage);
        Map<String, Object> resultMap = (Map<String, Object>) postDeploymentResult.getData();
        List<Map<String, Object>> resultData = (List<Map<String, Object>>) resultMap.get("data");

        assertTrue(postDeploymentResult.getSuccess());
        Assertions.assertEquals(null, postDeploymentResult.geMessage());
        Assertions.assertEquals(0, resultData.size());

        //場景2 : fullImage=null
        fullImage = null;
        try {
            postDeploymentResult = (DWServiceResult) target.postDeployment(cloud, area, appId, fullImage);
        } catch (DWArgumentException e) {
            assertTrue(postDeploymentResult.getSuccess());
            assertTrue(e.getMessage().equals("fullImage is null or empty"));
        }

        //場景3 : StatusCode=400
        fullImage = "oeebackend-2.1.0:1.1.1.1000.10163";
        new Expectations() {
            {
                mockStatusLine.getStatusCode();
                result = 400;
            }
        };
        try {
            postDeploymentResult = (DWServiceResult) target.postDeployment(cloud, area, appId, fullImage);
        } catch (DWException e) {
            assertTrue(postDeploymentResult.getSuccess());
            assertTrue(e.getMessage().equals("Post Deployment failed, status code = 400, please check the log for more information."));
        }
    }

    @Test
    @DisplayName("postRedployment")
    void test_postRedployment_case() throws Exception {

        String appId = "oee";
        String cloud = "Azure";
        String area = "test";
        String imageName = "oeebackend";
        String fullImage = "oeebackend-2.1.0:1.1.1.1000.10163";

        //場景1 : StatusCode=200

        Map<String, Object> areaMap = new HashMap<>();
        areaMap.put("area", area);
        areaMap.put("fullImage", fullImage);

        List<Map<String, Object>> cloudList = new ArrayList<>();
        cloudList.add(areaMap);
        Map<String, Object> imagedata = new HashMap<>();
        imagedata.put("Azure", cloudList);

        List<Map<String, Object>> resultData = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("imageName", imageName);
        resultMap.put("imagedata", imagedata);
        resultData.add(resultMap);

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("status", "success ");

        Map<String, Object> postDeploymentMap = new HashMap<>();
        postDeploymentMap.put("status", "success");

        new Expectations() {
            {
                new MockUp<DeployService>() {
                    @Mock
                    Object getProductVersion(String appId) {
                        DWServiceResult getProductVersion = new DWServiceResult();
                        getProductVersion.setData(resultData);
                        return getProductVersion;
                    }

                    @Mock
                    Object getLog(String imageName, String cloud, String area, String appId, String action, String id) {
                        DWServiceResult getLog = new DWServiceResult();
                        getLog.setData(logMap);
                        return getLog;
                    }

                    @Mock
                    Object postDeployment(String cloud, String area, String appId, String fullImage) {
                        DWServiceResult postDeployment = new DWServiceResult();
                        postDeployment.setData(postDeploymentMap);
                        return postDeployment;
                    }
                };
            }
        };
        DWServiceResult postRedploymentResult = (DWServiceResult) target.postRedployment(cloud, area, appId, imageName);
        Map<String, Object> postRedploymentMap = (Map<String, Object>) postRedploymentResult.getData();
        assertTrue(postRedploymentResult.getSuccess());
        Assertions.assertEquals(null, postRedploymentResult.geMessage());
        Assertions.assertEquals("success", postRedploymentMap.get("status"));

        //場景2 :  imageName=null
        resultMap.remove("imageName");

        new Expectations() {
            {
                new MockUp<DeployService>() {
                    @Mock
                    Object getProductVersion(String appId) {
                        DWServiceResult getProductVersion = new DWServiceResult();
                        getProductVersion.setData(resultData);
                        return getProductVersion;
                    }
                };
            }
        };
        try {
            postRedploymentResult = (DWServiceResult) target.postRedployment(cloud, area, appId, imageName);

        } catch (Exception e) {
            assertTrue(postRedploymentResult.getSuccess());
            assertTrue(e.getMessage().equals("IDeployService.postRedployment failed! -> oee can't found the corresponding oeebackend"));
        }

        //場景3 :  areaList=null

        resultMap.put("imageName", imageName);
        imagedata.put("Azure", new ArrayList<>());

        new Expectations() {
            {
                new MockUp<DeployService>() {
                    @Mock
                    Object getProductVersion(String appId) {
                        DWServiceResult getProductVersion = new DWServiceResult();
                        getProductVersion.setData(resultData);
                        return getProductVersion;
                    }
                };
            }
        };
        try {
            postRedploymentResult = (DWServiceResult) target.postRedployment(cloud, area, appId, imageName);

        } catch (Exception e) {
            assertTrue(postRedploymentResult.getSuccess());
            assertTrue(e.getMessage().equals("IDeployService.postRedployment failed! -> oee can't found the corresponding Azure"));
        }

        //場景4 :  areaList=null

        imagedata.put("Azure", cloudList);
        areaMap.remove("area");

        new Expectations() {
            {
                new MockUp<DeployService>() {
                    @Mock
                    Object getProductVersion(String appId) {
                        DWServiceResult getProductVersion = new DWServiceResult();
                        getProductVersion.setData(resultData);
                        return getProductVersion;
                    }
                };
            }
        };
        try {
            postRedploymentResult = (DWServiceResult) target.postRedployment(cloud, area, appId, imageName);

        } catch (Exception e) {
            assertTrue(postRedploymentResult.getSuccess());
            assertTrue(e.getMessage().equals("IDeployService.postRedployment failed! -> oee can't found the corresponding test"));
        }

        //場景5 :  status=IN_PROGRESS

        areaMap.put("area", area);
        logMap.put("status", "IN_PROGRESS");

        new Expectations() {
            {
                new MockUp<DeployService>() {
                    @Mock
                    Object getProductVersion(String appId) {
                        DWServiceResult getProductVersion = new DWServiceResult();
                        getProductVersion.setData(resultData);
                        return getProductVersion;
                    }

                    @Mock
                    Object getLog(String imageName, String cloud, String area, String appId, String action, String id) {
                        DWServiceResult getLog = new DWServiceResult();
                        getLog.setData(logMap);
                        return getLog;
                    }
                };
            }
        };
        try {
            postRedploymentResult = (DWServiceResult) target.postRedployment(cloud, area, appId, imageName);

        } catch (Exception e) {
            assertTrue(postRedploymentResult.getSuccess());
            assertTrue(e.getMessage().equals("IDeployService.postDeployment failed! -> 目前為正在部屬中狀態，無法進行重新部屬"));
        }
    }
}