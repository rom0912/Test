package com.romy.notification.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class FcmConfig {

	@Bean
	MessageChannel fcmCh() {
		return new ExecutorChannel(fcmTask());
	}

	@Bean
	ClientHttpRequestFactory fcmFac() {
		SimpleClientHttpRequestFactory fcmFac = new SimpleClientHttpRequestFactory();
		fcmFac.setReadTimeout(2000);
		fcmFac.setConnectTimeout(2000);
		return fcmFac;
	}

	@Bean
	TaskExecutor fcmTask() {
		ThreadPoolTaskExecutor fcmTask = new ThreadPoolTaskExecutor();
		fcmTask.setThreadNamePrefix("fcmTask-");
		fcmTask.setCorePoolSize(5);
		fcmTask.setMaxPoolSize(10);
		fcmTask.setQueueCapacity(0);
		fcmTask.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		return fcmTask;
	}

	@Bean
	@ServiceActivator(inputChannel = "fcmCh")
	MessageHandler fcmReq() {

		HttpRequestExecutingMessageHandler fcmReq = new HttpRequestExecutingMessageHandler("{url}");
		fcmReq.setRequestFactory(fcmFac());
		fcmReq.setHeaderMapper(fcmHeaderMapper());
		fcmReq.setHttpMethod(HttpMethod.POST);
		fcmReq.setCharset("UTF-8");
		fcmReq.setExpectedResponseType(String.class);
		Map<String, Expression> uriVariableExpressions = new HashMap<String, Expression>();
		ExpressionParser parser = new SpelExpressionParser();
		uriVariableExpressions.put("url", parser.parseExpression("headers[\'url\']"));
		fcmReq.setUriVariableExpressions(uriVariableExpressions);
		fcmReq.setExtractPayload(true);
		return fcmReq;
	}

	@Bean
	HeaderMapper fcmHeaderMapper() {

		DefaultHttpHeaderMapper fcmHeaderMapper = new DefaultHttpHeaderMapper();
		String[] s = { "*" };
		fcmHeaderMapper.setOutboundHeaderNames(s);
		fcmHeaderMapper.setInboundHeaderNames(s);
		fcmHeaderMapper.setUserDefinedHeaderPrefix("");
		return fcmHeaderMapper;
	}
	
}
