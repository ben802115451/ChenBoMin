package com.digiwin.deploy.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.digiwin.app.dao.serializer.DWDataSetDeserializer;
import com.digiwin.app.data.DWDataSet;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.metadata.DWMetadata;
import com.digiwin.app.metadata.DWMetadataContainer;
import com.digiwin.app.metadata.loader.DWMetadataLoaderManager;
import com.digiwin.app.metadata.rdbms.DWRdbmsField;
import com.digiwin.app.metadata.rdbms.DWRdbmsMetadata;
import com.digiwin.app.metadata.rdbms.DWRdbmsUtils;
import com.digiwin.app.module.spring.SpringContextUtils;
import com.digiwin.deploy.service.test.DWAttributeDeserializer;
import com.google.gson.Gson;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;

public class DWDeploymentEnvironment {

	private static boolean init = false;
	
	private static Map<String, DWRdbmsMetadata> metadatas;
	
	public synchronized static void init() {
	    	
	    if (DWDeploymentEnvironment.init) return;
	    
	    // 初始化 spring context
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext();
		context.refresh();
	
		SpringContextUtils contextUtils = new SpringContextUtils();
		contextUtils.setApplicationContext(context);
	    
	    // 註冊 deserializer 
	    new DWDataSetDeserializer();
	    new DWAttributeDeserializer();
	    
	    initMetadatas();
		new Expectations() {
			{
				new MockUp<DWMetadataLoaderManager>() {
					@Mock
					public DWMetadata<?> load(Class<? extends DWMetadata<?>> metadataType, String name) {
	
						if (metadataType == DWRdbmsMetadata.class) {
						
							return metadatas.get(name);
						}
						else {
							
							return null;
						}
					}
				};
			}
		};
	    
	    DWDeploymentEnvironment.init = true;
	}
	
	@SuppressWarnings("unchecked")
	static void initMetadatas() {
	
		metadatas = new HashMap<>();

		Gson gson = DWGsonProvider.getGson();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				DWDeploymentEnvironment.class.getResourceAsStream("/rdb-metadata.json")));
		
		List<Object> rawData = gson.fromJson(bufferedReader, List.class);
		DWRdbmsMetadata metadata;
		for (Object rawMetadata : rawData) {
			
			metadata = gson.fromJson(gson.toJson(rawMetadata), DWRdbmsMetadata.class);
			metadatas.put(metadata.getName(), metadata);
		}
		
//		DWRdbmsMetadata metadata2 = new DWRdbmsMetadata(DBConstants.DEPLOY_INFO);
//		metadata.addField(new DWRdbmsField(DBConstants.DEPLOY_ID, "int", 0, 0, false, true, false));
//		metadata.addField(new DWRdbmsField(DBConstants.APP_ID, "varchar", 0, 0, false, false, false));
//		metadata.addField(new DWRdbmsField(DBConstants.DEPLOY_AREA, "varchar", 0, 0, false, false, false));
//		metadata.addField(new DWRdbmsField(DBConstants.DEPLOY_VERSION, "varchar", 0, 0, false, false, false));
//		metadata.addField(new DWRdbmsField(DBConstants.IS_PUBLISH, "bit", 0, 0, false, false, false));
//		metadata.addField(new DWRdbmsField(DBConstants.CREATE_USER, "varchar", 0, 0, false, false, false));
//		metadata.addField(new DWRdbmsField(DBConstants.MODIFY_USER, "varchar", 0, 0, false, false, false));
//		
//		metadatas.put(metadata.getName(), metadata);
	}
}
