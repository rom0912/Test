package com.romy.notification.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotiListenerMapper {

	List<Map<String, Object>> selNotiSendList(@Param("msgTypeCd") String msgTypeCd);
	
	void insertNotiListener(Map<String, Object> paramMap);
	
	Integer updateNotiListener(Map<String, Object> mapParam);
	
	List<String> selNotiListenerByPush(Map<String, String> paramMap);
	
	void updateNotiListenerByPush(Map<String, Object> paramMap);
	
}
