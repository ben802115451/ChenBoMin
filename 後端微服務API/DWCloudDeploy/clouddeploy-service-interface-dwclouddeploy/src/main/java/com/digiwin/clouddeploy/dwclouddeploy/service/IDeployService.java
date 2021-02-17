package com.digiwin.clouddeploy.dwclouddeploy.service;

import com.digiwin.app.service.DWService;

public interface IDeployService extends DWService{
    /**取得所有版本
     *
     * @param appId //哪個應用
     * @return
     * @throws Exception
     */
    public Object getProductVersion(String appId) throws Exception;


    /**取得特定區間版本
     *
     * @param imageName //應用/應用版本
     * @param maxfullImage //最大的 Image 名稱與版本
     * @param minfullImage //最小的 Image 名稱與版本
     * @param orderBy //排序用的 ASC | DESC,沒有傳預設是DESC
     * @return
     * @throws Exception
     */
    public Object getSpecificVersion(String imageName,String maxfullImage,String minfullImage,String orderBy) throws Exception;


    /**取得部屬歷程
     *
     * @param  imageName
     * @param cloud //哪朵雲
     * @param area //哪一區
     * @param appId //哪個應用
     * @param action  //1. 代表這次是部署後看LOG  2. 代表畫面上的察看LOG歷程
     * @param id //StageFlowNode 的 ID（若沒有填寫，擇取最後一筆）
     * @return
     * @throws Exception
     */
    public Object getLog(String imageName, String cloud, String area, String appId, String action, String id) throws Exception;


    /**取得當前部屬狀態
     *
     * @param  imageName
     * @param cloud //哪朵雲
     * @param area //哪一區
     * @param appId //哪個應用
     * @param action  //1. 代表這次是部署後看LOG  2. 代表畫面上的察看LOG歷程
     * @return
     * @throws Exception
     */
    public Object getLogView(String imageName, String cloud, String area, String appId, String action) throws Exception;


    /**部屬應用
     *
     * @param cloud //哪朵雲
     * @param area //哪一區
     * @param appId //哪個應用
     * @param fullImage //image版號
     * @return
     * @throws Exception
     */
    public Object postDeployment(String cloud,String area,String appId,String fullImage) throws Exception;


    /**重新部屬
     *
     * @param cloud //哪朵雲
     * @param area //哪一區
     * @param appId //哪個應用
     * @param imageName //哪一端
     * @return
     * @throws Exception
     */
    public Object postRedployment(String cloud,String area,String appId,String imageName) throws Exception;
}