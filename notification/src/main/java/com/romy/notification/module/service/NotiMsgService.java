package com.romy.notification.module.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.notification.common.logger.Log;
import com.romy.notification.entity.NotiMsg;
import com.romy.notification.repository.NotiMsgMapper;
import com.romy.notification.repository.NotiMsgRepository;

@Service
public class NotiMsgService {

	@Autowired
	private NotiMsgMapper notiMsgMapper;
	
	@Autowired
	private NotiMsgRepository notiMsgRepository;
	
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
	
	/**
	 * 알림 메시지 등록
	 * @param mapMsg
	 * @return
	 */
	public Long insertNotiMsgByJpa(Map<String, String> mapMsg) {
		
		NotiMsg msg = new NotiMsg();
		
		msg.setMsgTypeCd(mapMsg.get("msgTypeCd"));
		msg.setTitle(mapMsg.get("title"));
		msg.setMessage(mapMsg.get("message"));
		msg.setModuleName(mapMsg.get("moduleName"));
		
		String sender = mapMsg.get("sender"); 
		if(sender != null && !"".equals(sender)) {
			msg.setSender(sender);
		}
		
		msg = notiMsgRepository.save(msg);
		return msg.getMsgId();
	}
	
	
	/**
	 * 알림 메시지 등록
	 * @param mapMsg
	 * @return
	 */
	public Long insertNotiMsg(Map<String, String> mapMsg) {
		
		Long msgId = notiMsgMapper.getNotiMsgId();
		mapMsg.put("msgId", msgId.toString());
		
		notiMsgMapper.insertNotiMsg(mapMsg);
		
		return msgId;
	}
	
	/**
	 * 발송자 정보 수정
	 * @param paramMap
	 */
	public void upddateNotiMsgSender(Map<String, Object> paramMap) {
		
		notiMsgMapper.upddateNotiMsgSender(paramMap);
	}
	
	/**
	 * push 메시지 리스트
	 * @return
	 */
	public List<Map<String, String>> selNotiMsgByPush() {
		
		return notiMsgMapper.selNotiMsgByPush();
	}

	/**
	 * 푸시 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selNotiPushMsg(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> mapResult = new HashMap<>();
		
		List pushId = (ArrayList) paramMap.get("pushIds");
		
		try {
			if(pushId != null) {
				Collection<Long> pushIds = new ArrayList<>();
				
				for (Integer i = 0, len = pushId.size(); i < len; i++) {
					Integer notiId = (Integer) pushId.get(i);
					pushIds.add(notiId.longValue());
				}
				
				LocalDateTime curDate = LocalDateTime.now();
				
				List<NotiMsg> list = notiMsgRepository.findByMsgIdInOrderBySendDateDesc(pushIds);
				List<Map<String, Object>> listData = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> listHist = new ArrayList<Map<String,Object>>();
				
				for (NotiMsg data : list) {
					Map<String, Object> msg = new HashMap<String, Object>();
					msg.put("pushId", data.getMsgId());
					msg.put("sendDate", format.format(data.getSendDate()));
					msg.put("createDate", format.format(data.getCreateDate()));
					msg.put("title", data.getTitle());
					msg.put("message", data.getMessage());
					msg.put("note", data.getNote());
					
					if(curDate.compareTo(data.getSendDate()) > -1) {
						listHist.add(msg);
					} else {
						listData.add(msg);
					}
				}
				
				mapResult.put("list", listData);
				mapResult.put("histList", listHist);
			}
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
		
		return mapResult;
	}
	
	/**
	 * 푸시 수정
	 * @param paramMap
	 * @throws Exception
	 */
	public void updateNotiPushMsg(Map<String, Object> paramMap) throws Exception {
		// push id
		String pushId = (String) paramMap.get("pushId");
		
		Optional<NotiMsg> data = notiMsgRepository.findById(Long.parseLong(pushId));
		if(data.isPresent()) {
			// 제목
			String title = (String) paramMap.get("title");
			// 내용
			String message = (String) paramMap.get("message");
			// 발송일자
			String sendDate = (String) paramMap.get("sendDate");
			
			NotiMsg msg = data.get();
			msg.setTitle(title);
			msg.setMessage(message);
			msg.setSendDate(LocalDateTime.parse(sendDate, format));
			
			notiMsgRepository.save(msg);
		}
	}
	
	/**
	 * 푸시 메시지 삭제
	 * @param paramMap
	 */
	public void deleteNotiMsgByPush(Map<String, Object> paramMap) {
		notiMsgMapper.deleteNotiMsgByPush(paramMap);
	}
	
}
