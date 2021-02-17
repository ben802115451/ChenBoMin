package com.digiwin.developer.dwdeveloper.service.impl;

import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWPagableQueryInfo;
import com.digiwin.app.dao.DWQueryValueOperator;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.developer.dwdeveloper.service.IDownloadCenterService;
import com.digiwin.http.client.DWHttpClient;
import com.digiwin.http.client.utils.DWRequestHeaderUtils;
import com.digiwin.http.client.utils.DWURIBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Miko
 */
public class DownloadCenterService implements IDownloadCenterService {
    private static final Log log = LogFactory.getLog(DownloadCenterService.class);

    static String iamUrlUserTenantApplicationPath = DWApplicationConfigUtils.getProperty("iamUrlUserTenantApplication");//寫在properties 10/23 falcon review

    @Autowired
    DWHttpClient httpClient;

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    static final String GOODS_CODE = "id";
    static final String EXPIRED = "expired";

    @Override
    public Object getList(DWPagableQueryInfo queryInfo) throws Exception { //getList(....) 10/23 falcon review
        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setTenantEnabled(false);
        option.setManagementFieldEnabled(false);

        List<String> result = this.queryGoods(); //goodsCode兩個條件整合成一個 10/23 falcon review
        DWDataSet dataset = new DWDataSet();

        if (result.size() > 0) { //加else table 10/23 falcon review
            //取得DB裡商品上架的資料(status=1)
            queryInfo.setTableName(DBConstants.FILE_MANAGEMENT);
            queryInfo.addFieldInfo(DBConstants.STATUS, DWQueryValueOperator.Equals, "1");
            queryInfo.addFieldInfo(DBConstants.GOODS_CODE, DWQueryValueOperator.In, result.toArray()); // ?呢 falcon，這邊組出來的?不對，因為只給一個BOSS出現多個應用 //棄用的不要再用 10/23 falcon review

            dataset = this.dao.select(queryInfo, option);
        }

        return dataset;
    }

    //呼叫/api/iam/v2/user/tenant/application，取得該租戶下授權的應用 (回傳goodsCode goodsName)
    private List<String> queryGoods() throws Exception { //queryGoods /修飾詞 private 10/23 falcon review
        DWDataSetOperationOption option = new DWDataSetOperationOption();

        List<Map<String, Object>> result = null;
        List<String> goodsCodeList = new ArrayList<>();

        URI uri = DWURIBuilder.create("iam", iamUrlUserTenantApplicationPath).build();
        HttpGet httpGetMethod = new HttpGet(uri);
        result = httpClient.execute(httpGetMethod, List.class, DWRequestHeaderUtils::getIamApiRequiredHeaders);

        String goodsCode;

        //處理呼叫完/api/iam/v2/user/tenant/application後，只需goodsCode、goodsName、expired，且expired為false(未到期)
        for (Map<String, Object> map : result) {
            goodsCode = map.get(GOODS_CODE).toString();
            String expired = map.get(EXPIRED).toString(); //型態要跟原本呼叫api回來的一致，EX這個原本是boolean 10/23 falcon review

            if (expired.equals("false")) { //改成.equals() 10/23 falcon review
                goodsCodeList.add(goodsCode);
            }
        }

        log.info(">>>get queryGoods  = " + goodsCodeList);

        return goodsCodeList; //不用重複寫 這邊就回傳好goodsCodeList 10/23 falcon review
    }
}
