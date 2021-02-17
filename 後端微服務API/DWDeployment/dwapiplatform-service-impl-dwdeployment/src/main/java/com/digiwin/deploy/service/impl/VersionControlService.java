package com.digiwin.deploy.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.digiwin.app.dao.DWDao;
import com.digiwin.app.service.DWService;
import com.digiwin.deploy.service.IVersionControlService;

/**
 * 版本控制服務實現
 * @author falcon
 *
 */
public class VersionControlService implements IVersionControlService {

	@Autowired
	@Qualifier("Dao")
	private DWDao dao;
	
	@Override
	public Object getApplication() throws Exception {

//		String version = null;
//		if (DWService.class.getPackage() != null) {
//			
//			version = DWService.class.getPackage().getImplementationVersion();
//		}
		
		return "unknown";
	}

	@Override
	public Object getPlatform() throws Exception {

		String version = null;
		if (DWService.class.getPackage() != null) {
			
			version = DWService.class.getPackage().getImplementationVersion();
		}
		
		return version;
	}
	
	@Override
	public Object updateProduct(String version, Map<String, Object> params) throws Exception {

		Runtime.getRuntime().exec("notepad");
		System.exit(1);
		return null;
	}
}
