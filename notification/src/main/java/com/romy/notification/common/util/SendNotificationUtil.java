package com.romy.notification.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.romy.notification.module.service.NotiListenerService;
import com.romy.notification.module.service.NotiMsgService;

@Component
public class SendNotificationUtil {

	@Autowired
	private NotiMsgService notiMsgService;

	@Autowired
	private NotiListenerService notiListenerService;

	@Autowired
	private FcmGateWay fcmGw;

	@Autowired
	private ObjectMapper mapper;
	
	@Value("${push.fcm.url}")
    private String FCM_URL;

	Properties pt = new Properties();

	/**
	 * 메일 발송
	 * 
	 * @param mailList
	 */
	@Async("asyncTask")
	public void sendEmail(List<Map<String, Object>> mailList) {

		String mailHost = "";
		String id = "";

		JavaMailSenderImpl sender = null;
		Authenticator auth = null;
		Session session = null;

		for (Map<String, Object> info : mailList) {
			try {

				String tempMailHost = (String) info.get("mailHost");
				id = (String) info.get("mailUsername");
				if (!mailHost.equals(tempMailHost)) {
					mailHost = tempMailHost;
					sender = new JavaMailSenderImpl();
					sender.setHost(mailHost);
					sender.setPort((Integer) info.get("mailPort"));
					sender.setUsername(id);
					sender.setPassword((String) info.get("mailPassword"));
					sender.setDefaultEncoding("UTF-8");
					sender.setProtocol("smtp");
			        
					if("Y".equals(String.valueOf(info.get("tlsYn")))) {
						pt.put("mail.smtp.host", tempMailHost);
						pt.put("mail.smtp.port", info.get("mailPort"));
						pt.put("mail.smtp.auth", "true");
						pt.put("mail.smtp.starttls.enable", "true");
						
						sender.setJavaMailProperties(pt);
						
						auth = new MailAuth(id, (String) info.get("mailPassword"));
						session = Session.getDefaultInstance(pt, auth);
					} else {
						auth = null;
						session = null;
					}

					notiMsgService.upddateNotiMsgSender(info);
				}
				
				if(session != null) {
					MimeMessage msg = new MimeMessage(session);

		            msg.setSentDate(new Date());
		            
		            msg.setFrom(new InternetAddress(id, "관리자"));
		            InternetAddress to = new InternetAddress((String) info.get("listener"));         
		            msg.setRecipient(Message.RecipientType.TO, to);            
		            msg.setSubject((String) info.get("title"), "UTF-8");            
		            msg.setDataHandler(new DataHandler(new ByteArrayDataSource((String) info.get("message"), "text/html;charset=utf-8")));
		            
		            Transport.send(msg);
				} else {
					sender.send(new MimeMessagePreparator() {

						@Override
						public void prepare(MimeMessage message) throws Exception {
							MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

							mimeMessageHelper.setTo((String) info.get("listener"));
							mimeMessageHelper.setSubject((String) info.get("title"));
							mimeMessageHelper.setText((String) info.get("message"), true);
							mimeMessageHelper.setFrom((String) info.get("mailAddress"));
						}
					});
				}

				info.put("statuscd", "SUCCESS");
				notiListenerService.updateNotiListener(info);
			} catch (Exception e) {
				info.put("statuscd", "FAIL");
				info.put("errormsg", e.getMessage());
				notiListenerService.updateNotiListener(info);
			}
		}
	}
	
	/**
	 * fcm AccessToken 발급
	 * @param module
	 * @return
	 * @throws IOException
	 */
	private String getFcmAccessToken(String module) throws IOException {
		
		String fireBasePath = "fcm/fcm_service_key_" + module.toLowerCase() + ".json";
		
		GoogleCredentials credentials = GoogleCredentials
				.fromStream(new ClassPathResource(fireBasePath).getInputStream())
				.createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
		credentials.refreshIfExpired();
		
		return credentials.getAccessToken().getTokenValue();
	}

	/**
	 * push 발송
	 * @param pushList
	 * @throws Exception
	 */
	@Async("asyncTask")
	public void sendPush(List<Map<String, String>> pushList) throws Exception {

		for (Map<String, String> info : pushList) {
			
			String title 		= (String) info.get("title");
			String body 		= (String) info.get("message");
			String projectId 	= (String) info.get("projectId");
			String moduleName 	= (String) info.get("moduleName");
			String msgId 		= (String) info.get("msgId");
			
			List<String> devices = notiListenerService.selNotiListenerByPush(info);
			
			boolean isLoop = true;
			int fromIndex = 0;
			int toIndex = 0;
			while (isLoop) {

				int devicesSize = devices.size();
				List<String> cutDevices = new ArrayList<>();

				if (devicesSize > 1000) {
					toIndex += 1000;
				} else {
					toIndex = devicesSize;
				}

				if (devicesSize <= toIndex) {
					toIndex = devicesSize;
					isLoop = false;
				}
				cutDevices = devices.subList(fromIndex, toIndex);

				fromIndex = toIndex;

				String param = buildupMsg(title, body, cutDevices);
				String resJson = fcmGw
						.sendReq(MessageBuilder.withPayload(param)
								.setHeader("url", FCM_URL + projectId + "/messages:send")
								.setHeader("Content-Type", "application/json;charset=UTF-8")
								.setHeader("Authorization", "Bearer " + getFcmAccessToken(moduleName)).build())
						.getPayload().toString();

				// 다시 보낼 디바이스 토큰을 찾는다.
				List<String> failList = new ArrayList<String>();
				List<String> successList = new ArrayList<String>();
				if (resJson != null && !resJson.equals("")) {
					Map<String, Object> rsMap = mapper.readValue(resJson, new HashMap<String, Object>().getClass());
					if (rsMap.containsKey("failure") && Integer.parseInt(rsMap.get("failure").toString()) > 0
							&& rsMap.containsKey("results")) {
						// error 건수가 있으면
						List<Map<String, Object>> results = (List<Map<String, Object>>) rsMap.get("results");
						int tokenLocation = 0;
						for (Map<String, Object> result : results) {
							if (result.containsKey("error")) {
								failList.add(cutDevices.get(tokenLocation));
							} else {
								successList.add(cutDevices.get(tokenLocation));
							}
							tokenLocation++;
						}
					}
				}
				
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("failList", failList);
				paramMap.put("successList", successList);
				paramMap.put("msgId", msgId);
				
				notiListenerService.updateNotiListenerByPush(paramMap);
			}
		}
	}

	private String buildupMsg(String title, String body, List<String> devices) throws Exception {

		if (devices == null || devices.size() == 0) {
			return null;
		}

		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> notificationMap = new HashMap<>();
		notificationMap.put("alert", title);
		notificationMap.put("title", title);
		notificationMap.put("body", body);
		notificationMap.put("sound", "default");
		notificationMap.put("vibrate", "true");
		paramMap.put("notification", notificationMap);
		/**
		 * 안드로이드에서 data 로 뽑는 파라메터는 pushId = "" , message = "" String 구조
		 */
		paramMap.put("ttl", "86400s");
		if (devices.size() == 1) {
			paramMap.put("to", devices.get(0));
		} else {
			paramMap.put("registration_ids", devices);
		}

		return mapper.writeValueAsString(paramMap);
	}
	
}
