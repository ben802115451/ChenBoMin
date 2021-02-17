package com.digiwin.deploy.service.impl;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.digiwin.app.dao.DWServiceResultBuilder;
import com.digiwin.deploy.service.IConfigTestService;

public class ConfigTestService implements IConfigTestService {

	@Override
	public Object getValue() throws Exception {
		
		System.setProperty("apollo.meta", "http://10.40.42.115:8080");
		
		System.setProperty("app.id", "test");
		
		Config config = ConfigService.getConfig("test");
		
		String value = config.getProperty("test", "000000");
		
		return DWServiceResultBuilder.build("新增成功", value);
	}
}
