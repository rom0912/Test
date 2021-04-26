package com.romy.auth.common.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomMessage {

	private Map<String, String> msg;
	
	private List<String> listener;
	
	private String routeKey;
	
	private String topic;
	
	protected CustomMessage() {
		
	}
	
	public CustomMessage(Map<String, String> msg, List<String> listener, String routeKey, String topic) {
		this.msg = msg;
		this.listener = listener;
		this.routeKey = routeKey;
		this.topic = topic;
	}

	public Map<String, String> getMsg() {
		return msg;
	}

	public void setMsg(Map<String, String> msg) {
		this.msg = msg;
	}

	public List<String> getListener() {
		return listener;
	}

	public void setListener(List<String> listener) {
		this.listener = listener;
	}
	
	public String getRouteKey() {
		return routeKey;
	}

	public void setRouteKey(String routeKey) {
		this.routeKey = routeKey;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
    public String toString() {
		
		try {
			return "{msg='" + new ObjectMapper().writeValueAsString(msg) + '\'' +
	                ", listener='" + new ObjectMapper().writeValueAsString(listener) + "'}";
		} catch (Exception e) {
			return "";
		}
    }
}
