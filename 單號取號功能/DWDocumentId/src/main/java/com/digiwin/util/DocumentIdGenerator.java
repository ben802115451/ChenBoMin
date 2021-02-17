package com.digiwin.util;

import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.data.exceptions.DWDataException;
import com.digiwin.app.redis.service.DWRedisService;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.utils.DWTenantUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 單號產生器<br>
 *
 * @author Bomin
 */
public class DocumentIdGenerator {

    private static final Log logger = LogFactory.getLog(DocumentIdGenerator.class);

    private DocumentIdFormatter formatter = new DocumentIdFormatter();

    @Autowired
    private DocumentIdFinder finder;

    @Autowired
    private DWRedisService redisTemplate;

    private String redisPrefixFolder = "DAP:DocumentId";

    /**
     * 產生單組單號
     *
     * @param setting 取號參數設定檔
     * @return 單號
     * @throws Exception
     */
    public String getId(DocumentIdSetting setting) throws Exception {
        if (setting != null && setting.getNumber() > 1)
            throw new DWDataException("setting.setNumber can't greater than 1");

        String result = generateId(setting).get(0);
        return result;
    }

    /**
     * 產生多組單號
     *
     * @param setting 取號參數設定檔
     * @return 單號
     * @throws Exception
     */
    public List<String> getIdList(DocumentIdSetting setting) throws Exception {
        List<String> result = generateId(setting);
        return result;
    }

    /**
     * 產生單據編號
     *
     * @param setting 取號參數設定檔
     * @return 單據編號清單
     * @throws Exception
     */
    private List<String> generateId(DocumentIdSetting setting) throws Exception {
        if (setting == null) throw new DWDataException("DocumentIdSetting can'n be null ");
        List<String> result = new ArrayList<String>();
        String group = formatter.format(setting);

        logger.debug("group:" + group);
        List<Long> serialIds = generateSerialId(group, setting); //產生流水號

        for (long serialId : serialIds) {
            result.add(formatter.format(group, serialId, setting.getSerialIdLength()));
            logger.debug("serialId:" + serialId);
        }
        return result;
    }

    /**
     * 產生流水號
     *
     * @param group   prefix+年月日8位組合
     * @param setting 取號參數設定檔
     * @return 流水號
     * @throws Exception
     */
    private List<Long> generateSerialId(String group, DocumentIdSetting setting) throws Exception {
        List<Long> result = new ArrayList<Long>();
        long currentId;

        StringBuilder redisKeyGroup = new StringBuilder();
        String appId = DWApplicationConfigUtils.getProperty("appId");
        redisKeyGroup.append(redisPrefixFolder).append(":").append(appId).append(":");
        if (setting.isTenant()) {
            Map<String, Object> profile = DWServiceContext.getContext().getProfile();
            redisKeyGroup.append(profile.get(DWTenantUtils.getIAMTenantSidKey())).append(":");
        }
        redisKeyGroup.append(group);

        //如果Redis沒有值時
        if (!redisTemplate.hasKey(redisKeyGroup.toString())) {

            int maxSeriaNo = 0;
            //由finder.getMaxSerialNo從DB找出最大流水號的單據編號
            String documentId = finder.getMaxSerialNo(group, setting);
            if (documentId != null && !documentId.equals("")) {
                maxSeriaNo = formatter.fetchSerialNo(documentId, group, setting.getSerialIdLength());
            }

            redisTemplate.set(redisKeyGroup.toString(), maxSeriaNo);
        }
        currentId = redisTemplate.incrBy(redisKeyGroup.toString(), setting.getNumber());

        // 計算流水號開始id
        long startId = currentId - setting.getNumber() + 1;
        for (long i = startId; i <= currentId; i++) {
            result.add(i);
            logger.debug("Fetch redis serialNo=" + i);
        }
        return result;
    }
}
