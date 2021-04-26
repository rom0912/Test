package com.romy.notification.module.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.notification.entity.NotiMng;
import com.romy.notification.repository.NotiMngMapper;
import com.romy.notification.repository.NotiMngRepository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class NotiMngService {

	@Autowired
	private NotiMngMapper notiMngMapper;
	
	@Autowired
	private NotiMngRepository notiMngRepository;
	
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

	/**
	 * 사용자 키에 해당하는 알림 리스트 조회
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> selNotiMngByUserKey(Map<String, Object> paramMap) {
		
		String userKey = (String) paramMap.get("userKey");
		
		List<NotiMng> list = notiMngRepository.findByUserKeyOrderByReadYnDescCreateDateDesc(userKey);
		
		List<Map<String, Object>> notiList = new ArrayList<>();
		
		for (NotiMng mng : list) {
			Map<String, Object> noti = new HashMap<>();
			
			noti.put("notiId", mng.getNotiId());
			noti.put("msgInfo", mng.getMsgInfo());
			noti.put("readYn", mng.getReadYn());
			noti.put("date", format.format(mng.getCreateDate()));
			
			notiList.add(noti);
		} 
		
		return notiList;
	}
	
	/**
	 * 읽음 표시
	 * @param paramMap
	 */
	public void updateNotiMngByRead(Map<String, Object> paramMap) {
		
		String notiId = (String) paramMap.get("notiId");
		
		NotiMng noti = notiMngRepository.findByNotiId(Long.parseLong(notiId));
		noti.setReadYn("Y");
		
		notiMngRepository.save(noti);
	}
	
	/**
	 * 알림 삭제
	 * @param paramMap
	 */
	public void deleteNotiMng(Map<String, Object> paramMap) {
		
		String notiId = (String) paramMap.get("notiId");
		
		notiMngRepository.deleteById(Long.parseLong(notiId));
	}
	
	/**
	 * 알림 전체삭제
	 * @param paramMap
	 */
	public void deleteNotiMngAll(Map<String, Object> paramMap) {
		
		notiMngMapper.deleteNotiMngByUserKey(paramMap);
	}
	
	/**
	 * 알림 등록
	 * @param jsonArr
	 */
	public void insertNotiMng(JSONArray jsonArr) {
		
		for (Integer i=0, size=jsonArr.size(); i < size; i++) {
			JSONObject json = (JSONObject) jsonArr.get(i);
			
			NotiMng noti = new NotiMng();
			
			noti.setUserKey(json.getString("userKey"));
			noti.setMsgInfo(json.getString("msgInfo").replaceAll("\"", "\'"));
			
			notiMngMapper.insertNotiMng(noti);
		}
	}
	
}
