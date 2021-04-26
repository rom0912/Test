package com.romy.notification.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotiMsgMapper {

	Long getNotiMsgId();
	
	Integer insertNotiMsg(Map<String, String> paramMap);
	
	Integer upddateNotiMsgSender(Map<String, Object> paramMap);
	
	List<Map<String, String>> selNotiMsgByPush();

	void deleteNotiMsgByPush(Map<String, Object> paramMap);
	
}
