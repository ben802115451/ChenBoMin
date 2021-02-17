package com.digiwin.deploy.service.test;

import java.lang.reflect.Type;

import com.digiwin.app.container.exceptions.DWSingletonAlreadyExistsException;
import com.digiwin.app.json.gson.DWGsonProvider;
import com.digiwin.app.metadata.DWAttribute;
import com.digiwin.app.metadata.DWNamedAttribute;
import com.digiwin.app.metadata.DWValueAttribute;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * 2019-6-13 DWAttribute 反序列化器 
 * @author falcon
 *
 */
public class DWAttributeDeserializer implements JsonDeserializer<DWAttribute> {

	/**
	 * 單例
	 */
	private static DWAttributeDeserializer singleton;
	
	/**
	 * 構造函數
	 */
	public DWAttributeDeserializer() {
		
		if (DWAttributeDeserializer.singleton != null) {
			
			throw new DWSingletonAlreadyExistsException(this);
		}
		
		// 2018-4-13 falcon 註冊至  DWDefaultParameters 中
		DWAttributeDeserializer.singleton = this;
		DWGsonProvider.registerTypeAdapter(DWAttribute.class, this);
	}
	
	/**
	 * 反序列化
	 */
	@Override
	public DWAttribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        
        // 此屬性用來判斷是否反序列化為 DWQueryCondition 或是 DWQueryFieldInfo
        final boolean hasValue = jsonObject.has("value");

        if (hasValue) {
        	
        	return context.deserialize(jsonObject, DWValueAttribute.class);
        }
     
        return context.deserialize(jsonObject, DWNamedAttribute.class);
	}

}
