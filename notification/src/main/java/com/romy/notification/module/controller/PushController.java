package com.romy.notification.module.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.romy.notification.common.logger.Log;
import com.romy.notification.module.service.NotiListenerService;
import com.romy.notification.module.service.NotiMsgService;

@Controller
@RequestMapping("/notification")
public class PushController {

	@Autowired
	private NotiMsgService notiMsgService;
	
	@Autowired
	private NotiListenerService notiListenerService;
	
	/**
	 * push 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/push/list")
	public ModelAndView pushList(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			Map<String, Object> mapResult = notiMsgService.selNotiPushMsg(paramMap); 
			mv.addObject("message", "OK");
			mv.addObject("scheduleList", mapResult.get("list"));
			mv.addObject("sendList", mapResult.get("histList"));
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("msg", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * push add
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/push/add")
	public ModelAndView pushAdd(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
	
		try {
			Map<String, String> msgInfo = (Map<String, String>) paramMap.get("msgInfo");
			List<String> listener = (List<String>) paramMap.get("listener");
			
			Long msgId = notiMsgService.insertNotiMsg(msgInfo);
			notiListenerService.insertNotiListener(listener, msgId);
			
			mv.addObject("message", "OK");
			mv.addObject("pushId", msgId);
			
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * push 메시지 수정
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/push/edit")
	public ModelAndView pushEdit(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			notiMsgService.updateNotiPushMsg(paramMap);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 푸시 메시지 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/push/delete")
	public ModelAndView pushDelete(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			notiMsgService.deleteNotiMsgByPush(paramMap);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
}
