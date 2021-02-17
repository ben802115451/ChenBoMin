package com.digiwin.util;

import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.dao.DWDaoImpl;
import com.digiwin.app.data.exceptions.DWDataException;
import com.digiwin.app.redis.service.DWRedisService;
import com.digiwin.app.service.DWServiceContext;
import mockit.*;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DocumentIdGeneratorTest {

    @Tested
    DocumentIdGenerator target;

    @Injectable
    DocumentIdFinder documentIdFinder;

    @Injectable
    DWRedisService dwRedisService;

    @Mocked
    DWApplicationConfigUtils dwApplicationConfigUtils;

    DocumentIdFormatter formatter = new DocumentIdFormatter();

    @Mocked
    DWServiceContext dwServiceContext;

    @Test
    void generateId() throws Exception {

        DocumentIdSetting setting = new DocumentIdSetting("TEST", "TEST");
        setting.setPrefix("TEST");
        setting.setDate(2020, 01, 01);
        setting.setSerialIdLength(4);
        setting.setNumber(1);
        setting.setDao(new DWDaoImpl(new QueryRunner()));

        String group = "TEST20200101";
        String redisKeyGroup1 = "DAP:DocumentId:test:tenantSid:TEST20200101";

        //場景1 redis hasKey && isTenant

        new Expectations() {
            {
                Map<String, Object> profile = new HashMap<String, Object>();
                profile.put("tenantSid", "tenantSid");
                dwServiceContext.getProfile();
                result = profile;

                formatter.format(setting);
                result = group;

                DWApplicationConfigUtils.getProperty("appId");
                result = "test";

                dwRedisService.hasKey(redisKeyGroup1);
                result = true;

                dwRedisService.incrBy(redisKeyGroup1, setting.getNumber());
                result = 1;
            }
        };

        String result = target.getId(setting);
        assertEquals("TEST202001010001", result);

        //場景2  redis has'n Key &&  documentId != null && number = 3

        setting.disableTenant();
        setting.setNumber(3);
        String redisKeyGroup2 = "DAP:DocumentId:test:TEST20200101";

        new Expectations() {

            {
                dwRedisService.hasKey(redisKeyGroup2);
                result = false;

                documentIdFinder.getMaxSerialNo(group, setting);
                result = "TEST202001010888";

                dwRedisService.incrBy(redisKeyGroup2, 3);
                result = 891;
            }
        };

        List<String> resultList = target.getIdList(setting);
        assertEquals(3, resultList.size());
        assertEquals("TEST202001010889", resultList.get(0));
        assertEquals("TEST202001010890", resultList.get(1));
        assertEquals("TEST202001010891", resultList.get(2));

        //場景3  redis has'n Key &&  documentId != null  &&  fetchSerialNo !isInteger

        setting.setNumber(1);

        new Expectations() {

            {
                documentIdFinder.getMaxSerialNo(group, setting);
                result = "TEST20200101000A";

                dwRedisService.incrBy(redisKeyGroup2, 1);
                result = 1;
            }
        };

        result = target.getId(setting);
        assertEquals("TEST202001010001", result);

        //場景4  redis has'n Key &&  documentId == null

        new Expectations() {

            {
                documentIdFinder.getMaxSerialNo(group, setting);
                result = null;

                dwRedisService.incrBy(redisKeyGroup2, 1);
                result = 1;
            }
        };

        result = target.getId(setting);
        assertEquals("TEST202001010001", result);

        //場景5 日期錯誤
        Assertions.assertThrows(ParseException.class, () -> setting.setDate(2020, 13, 1));

        //場景6 入參傳空

        Assertions.assertThrows(DWDataException.class, () -> new DocumentIdSetting("", ""));

        //場景7  getId setNumber >1

        setting.setNumber(2);
        Assertions.assertThrows(DWDataException.class, () -> target.getId(setting));

    }
}
