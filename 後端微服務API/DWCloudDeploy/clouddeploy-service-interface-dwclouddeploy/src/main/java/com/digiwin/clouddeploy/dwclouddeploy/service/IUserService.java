package com.digiwin.clouddeploy.dwclouddeploy.service;


import com.digiwin.app.service.DWService;



public interface IUserService extends DWService {

    //取得所有應用

    /**
     *
     * @return 該用戶所屬的應用清單列表
     * @throws Exception
     */
    public Object getAppList() throws Exception;
}
