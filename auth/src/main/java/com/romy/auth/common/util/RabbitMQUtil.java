package com.romy.auth.common.util;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;

@Component
public class RabbitMQUtil {
	
	private static RabbitTemplate rabbitTemplate;
	
	
	@Autowired
    public RabbitMQUtil(RabbitTemplate rabbitTemplate) {
    	
		RabbitMQUtil.rabbitTemplate = rabbitTemplate;
    }
	
	public static void sendNotification(Map<String, String> msgInfo, List<String> listener, String routeKey,
			String topic) {
		
		CustomMessage message = new CustomMessage(msgInfo, listener, routeKey, topic);
		rabbitTemplate.convertAndSend("romy", "com.romy.send.auth", message);
	}
	
	/**
	 * 로그인 로그 쌓기
	 * @param paramMap
	 * @throws Exception
	 */
	public static void sendLogLogin(Map<String, Object> paramMap) throws Exception {

		JSONObject json = new JSONObject();
		json.put("userKey", paramMap.get("userKey"));
		json.put("userName", paramMap.get("userName"));
		json.put("ip", paramMap.get("ip"));
		json.put("loginAgent", paramMap.get("loginAgent"));
		json.put("tenantKey", paramMap.get("tenantKey"));
		json.put("loginDupYn", "N");

		rabbitTemplate.convertAndSend("romy", "com.romy.log.login", json);
	}
}
