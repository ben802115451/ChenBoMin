package com.digiwin.marketmanagement.dwmarketmanagement.service.impl;

/**
 * data base constants
 *
 * @author falcon
 */
final class DBConstants {

    // 資料表
    static final String LAYOUT_ARRANGEMENT = "layout_arrangement";
    static final String CLOUD_THEME = "cloud_theme";
    static final String GOODS_DETAIL = "goods_detail";

    // 欄位
    static final String ID = "id";
    static final String LAST_MODIFY_USERID = "lastModifyUserId";
    static final String LAST_MODIFY_USERNAME = "lastModifyUserName";
    static final String AREA_TYPE = "areaType";

    static final String THEME_NAME = "name";
    static final String STATUS = "status";
    static final String CREATE_USERID = "createUserId";
    static final String CREATE_USERNAME = "createUserName";
    static final String SEQUENCE = "sequence";

    static final String THEME_ID = "themeId";
    static final String GOODS_TYPE = "goodsType";
    static final String GOODS_CODE = "goodsCode";
    static final String CATAGORY_ID = "categoryId";
    static final String GOODS_NAME = "goodsName";
    static final String GOODS_ARRANGEMENT_STYLE = "goodsArrangementStyle";

    // SQL
    static final String DELETE_AREATYPE = "DELETE FROM layout_arrangement WHERE ";
    static final String DELETE_ID_BY_SITIATION = "DELETE FROM layout_arrangement WHERE areaType = ? ";

    static final String SELECT_ACTIVE_LIST = "SELECT * FROM cloud_theme WHERE STATUS = 1 ORDER BY sequence ";
    static final String SELECT_INACTIVE_LIST = "SELECT * FROM cloud_theme WHERE STATUS = 0 ";
    static final String SELECT_ACTIVE_COUNT = "SELECT COUNT(STATUS) FROM cloud_theme WHERE STATUS = 1 ";
    static final String UPDATE_TO_ACTIVE = "UPDATE cloud_theme SET STATUS = 1 WHERE id = ? ";
    static final String UPDATE_TO_INACTIVE = "UPDATE cloud_theme SET STATUS = 0 WHERE id = ? ";
    static final String SELECT_ID = "SELECT * FROM cloud_theme WHERE ";
    static final String SELECT_ALL_FROM_CLOUDTHEME = "SELECT * FROM cloud_theme ";
    static final String DELETE_THEME = "DELETE FROM cloud_theme WHERE ";

    static final String SELECT_GOODS = "SELECT * FROM goods_detail WHERE ";
    static final String INSERT_GOODS = "INSERT INTO goods_detail (themeId, goodsCode) VALUES (?, ?) ";
    static final String SELECT_THEME_ID = "SELECT * FROM cloud_theme WHERE id = ? ";
}
