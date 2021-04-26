package com.romy.notification.common.config;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

public class JpaConverterJsonObject implements AttributeConverter<Object, String> {

	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	
	@Override
	public String convertToDatabaseColumn(Object meta) {
		try {
			return objectMapper.writeValueAsString(meta);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	@Override
	public JSONObject convertToEntityAttribute(String dbData) {
		try {
			return JSONObject.fromObject(dbData);
		} catch (Exception e) {
			return null;
		}
		
	}
	
}