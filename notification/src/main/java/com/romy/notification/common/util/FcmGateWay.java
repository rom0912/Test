package com.romy.notification.common.util;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface FcmGateWay {

	@Gateway(requestChannel = "fcmCh")
	Message<String> sendReq(Message<String> postMsg);
	
}
