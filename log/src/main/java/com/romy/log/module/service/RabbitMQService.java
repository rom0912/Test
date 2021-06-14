package com.romy.log.module.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.log.common.logger.Log;
import com.romy.log.repository.LogLoginMapper;

import net.sf.json.JSONObject;

@Service
public class RabbitMQService {

	@Autowired
	private LogLoginMapper logLoginMapper;
	
	
	/**
	 * 로그인 로그
	 * @param message
	 */
	@RabbitListener(queues = "sendLogLogin")
	public void receiveMessage(final JSONObject message) {
		
		Log.Debug();
		
		try {
			logLoginMapper.insertLogLogin(message);
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
	}
	
}
