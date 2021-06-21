package com.romy.notification.module.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.notification.common.util.SendNotificationUtil;
import com.romy.notification.repository.NotiListenerMapper;

@Service
public class NotiListenerService {

	@Autowired
	private NotiMsgService notiMsgService;
	
	@Autowired
	private NotiListenerMapper notiListenerMapper;
	
	@Autowired
	private SendNotificationUtil sendNotificationUtil;
	
	
	/**
	 * 알림 수신자 등록
	 * @param listListener
	 * @param notiId
	 */
	public void insertNotiListener(List<String> listListener, Long msgId) {
		
		List<Map<String, Object>> receiveList = new ArrayList<>();
		Map<String, Object> paramMap = null;
		
		for (String listener : listListener) {
			
			paramMap = new HashMap<>();
			
			paramMap.put("msgId", msgId);
			paramMap.put("listener", listener);
			
			receiveList.add(paramMap);
		}

		paramMap = new HashMap<String, Object>();
		paramMap.put("list", receiveList);
		
		notiListenerMapper.insertNotiListener(paramMap);
	}
	
	/**
	 * 알림 발송
	 */
	public void sendNotification() throws Exception {
		
		List<Map<String, Object>> mailList = notiListenerMapper.selNotiSendList("M");
		List<Map<String, String>> pushList = notiMsgService.selNotiMsgByPush();
		
		sendNotificationUtil.sendEmail(mailList);
		sendNotificationUtil.sendPush(pushList);
	}
	
	/**
	 * 발송 후 결과값 업데이트
	 * @param paramMap
	 */
	public void updateNotiListener(Map<String, Object> paramMap) {
		notiListenerMapper.updateNotiListener(paramMap);
	}
	
	/**
	 * push 수신자 목록
	 * @param paramMap
	 * @return
	 */
	public List<String> selNotiListenerByPush(Map<String, String> paramMap) {
		return notiListenerMapper.selNotiListenerByPush(paramMap);
	}
	
	/**
	 * push 결과 값 업데이트
	 * @param paramMap
	 */
	public void updateNotiListenerSuccess(Map<String, Object> paramMap) {
		notiListenerMapper.updateNotiListenerSuccess(paramMap);
	}
	
	public void updateNotiListenerFail(Map<String, Object> paramMap) {
		notiListenerMapper.updateNotiListenerFail(paramMap);
	}
}
