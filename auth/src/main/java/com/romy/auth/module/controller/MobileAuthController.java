package com.romy.auth.module.controller;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.romy.auth.common.logger.Log;
import com.romy.auth.module.service.MobileAuthService;

@CrossOrigin
@RestController
@RequestMapping("/auth/mobile")
public class MobileAuthController {

	@Autowired
	private MobileAuthService mobileAuthService;
	
	
	/**
	 * 모바일 로그인
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String, Object> mapResult = mobileAuthService.checkUserByMobile(paramMap, request);
		
		Set<String> keys = mapResult.keySet();
		for (String key : keys) {
			mv.addObject(key, mapResult.get(key));
		}
		
		Log.DebugEnd();
		
		return mv;
	}

	/**
	 * 아이디 유효성 체크
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/checkUserId")
	public ModelAndView checkUserId(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String, String> mapResult = mobileAuthService.checkUserIdByMobile(paramMap, request); 
		
		Set<String> keys = mapResult.keySet();
		for (String key : keys) {
			mv.addObject(key, mapResult.get(key));
		}
		
		return mv;
	}
	
}
