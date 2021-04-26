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
import com.romy.notification.module.service.NotiMngService;

@Controller
@RequestMapping("/notification")
public class NotificationController {

	@Autowired
	private NotiMngService notiMngService;
	
	@Autowired
	private NotiListenerService notiListenerService;
	
	
	/**
	 * 스케쥴러 배치 작업
	 * @param reques
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/batchJob")
	public void batchJob(HttpServletRequest reques, HttpServletResponse response) throws Exception {
		
		notiListenerService.sendNotification();
	}
	
	/**
	 * 알림 리스트 조회
	 * @param request
	 * @param respones
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noti/list")
	public ModelAndView notiList(HttpServletRequest request, HttpServletResponse respones,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			
			List<Map<String, Object>> list = notiMngService.selNotiMngByUserKey(paramMap);
			mv.addObject("message", "OK");
			mv.addObject("notiList", list);
			
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 알림 읽음 처리
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noti/update")
	public ModelAndView notiUpdate(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			notiMngService.updateNotiMngByRead(paramMap);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 알림 삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noti/delete")
	public ModelAndView notiDelete(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			notiMngService.deleteNotiMng(paramMap);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 알림 전체삭제
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/noti/deleteAll")
	public ModelAndView notiDeleteAll(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			notiMngService.deleteNotiMngAll(paramMap);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
}
