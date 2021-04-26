package com.romy.notification.module.service;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.notification.common.logger.Log;
import com.romy.notification.common.util.CustomMessage;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class RabbitMQService {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private NotiMsgService notiMsgService;
	
	@Autowired
	private NotiMngService notiMngService;
	
	@Autowired
	private NotiListenerService notiListenerService;
	
	/**
	 * 각 모듈에서 sendNotification 메시지 호출
	 * @param message
	 */
	@RabbitListener(queues = "sendNotification")
	public void receiveMessage(final CustomMessage message) {
		
		try {
			Map<String, String> msgInfo = message.getMsg();
			List<String> listener = message.getListener();
			
			String routeKey = message.getRouteKey();
			
			Long msgId = notiMsgService.insertNotiMsg(msgInfo);
			notiListenerService.insertNotiListener(listener, msgId);
			
			if(routeKey != null && !"".equals(routeKey)) {
				String topic = message.getTopic();
				
				JSONObject resJson = new JSONObject();
				
				resJson.put("paramMsg", message);
				resJson.put("resInfo", msgId);
				
				rabbitTemplate.convertAndSend(topic, routeKey, resJson);
			}
			
			// 임시로 스케쥴러 처럼 사용
			//notiListenerService.sendNotification();
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
	}
	
	/**
	 * 알림센터 RabbitMQ
	 * @param json
	 */
	@RabbitListener(queues = "sendNotiMng")
	public void receiveNotiMng(final JSONObject json) {
		
		try {
			String topic = json.getString("topic");
			String routeKey = json.getString("routeKey");
			
			JSONArray jsonArr = (JSONArray) json.get("data");
			notiMngService.insertNotiMng(jsonArr);
			
			rabbitTemplate.convertAndSend(topic, routeKey, json);
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
	}
}
