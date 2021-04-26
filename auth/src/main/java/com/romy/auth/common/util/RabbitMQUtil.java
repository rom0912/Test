package com.romy.auth.common.util;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	
	
}
