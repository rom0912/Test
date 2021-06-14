package com.romy.log.repository;

import org.apache.ibatis.annotations.Mapper;

import net.sf.json.JSONObject;

@Mapper
public interface LogLoginMapper {

	void insertLogLogin(JSONObject json);
	
}
