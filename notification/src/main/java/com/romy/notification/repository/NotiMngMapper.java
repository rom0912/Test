package com.romy.notification.repository;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.romy.notification.entity.NotiMng;

@Mapper
public interface NotiMngMapper {

	void insertNotiMng(NotiMng noti);

	void deleteNotiMngByUserKey(Map<String, Object> paramMap);
	
}
