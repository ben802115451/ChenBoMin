package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.dao.DWSQLExecutionResult;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.data.DWDataSetOperationOption;
import com.digiwin.marketmanagement.dwmarketmanagement.service.IRecommendedProductsService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecommendedProductsService implements IRecommendedProductsService {

    private static final Log log = LogFactory.getLog(RecommendedProductsService.class);

    @Autowired
    @Qualifier("Dao")
    private DWDao dao;

    @Autowired
    private GoodsTypeService goodsTypeService;

    static final String TABLE = "recommended_products";
    static final String DELETE_RECOMMENDED_PRODUCTS = "DELETE FROM recommended_products";
    static final String SELECT_RECOMMENDED_PRODUCTS = "SELECT * from recommended_products";

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object post(DWDataSet dataset) throws Exception {

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataset.getSourceMap().get(TABLE);

        if (dataList.size() > 10) {
            throw new DWArgumentException("post", "商品最多不超過10項!");
        }

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        dao.update(option, DELETE_RECOMMENDED_PRODUCTS); //刪除原始資料

        DWSQLExecutionResult result = dao.execute(dataset, option);

        return DWServiceResultBuilder.build("推薦商品更新成功", result);
    }

    @Override
    public Object get() throws Exception {

        DWDataSetOperationOption option = new DWDataSetOperationOption();
        option.setManagementFieldEnabled(false);

        List<Map<String, Object>> result = dao.select(option, SELECT_RECOMMENDED_PRODUCTS);
        List<String> goodsCodeList = new ArrayList<>();

        if (result.size() > 0) {
            for (Map<String, Object> resultMap : result) {
                goodsCodeList.add(resultMap.get("goodsCode").toString());
            }
        } else {
            return DWServiceResultBuilder.build(result);
        }

        List<Map<String, Object>> dataResult = (List<Map<String, Object>>) goodsTypeService.get(goodsCodeList);

        for (Map<String, Object> resultMap : result) {
            for (Map<String, Object> dataResultMap : dataResult) {
                if (resultMap.get("goodsCode").equals(dataResultMap.get("code"))) {
                    resultMap.put("categoryId", dataResultMap.get("categoryId"));
                    resultMap.put("goodsName", dataResultMap.get("displayName"));
                    break;
                }
            }
        }
        return DWServiceResultBuilder.build(result);
    }
}
