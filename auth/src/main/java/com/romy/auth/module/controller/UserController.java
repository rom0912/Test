package com.romy.auth.module.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.romy.auth.common.logger.Log;
import com.romy.auth.module.service.AuthTokenService;
import com.romy.auth.module.service.AuthUserService;

@CrossOrigin
@RestController
@RequestMapping("/auth/user")
public class UserController {

	
	@Autowired
	private AuthUserService authUserService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	
	/**
	 * 고객리스트 정보
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView userList(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			List<Map<String, Object>> list = authUserService.selectAuthHRUser(paramMap);
			mv.addObject("resInfo", list);
			mv.addObject("message", "OK");
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "FAIL");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 고객 추가
	 * @param request
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	public ModelAndView userAdd(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
		
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			Map<String, Object> mapResult = authUserService.insertAuthUser(paramMap);
			mv.addObject("message", mapResult.get("msg"));
			mv.addObject("tenantInfo", mapResult.get("tenantInfo"));
			mv.addObject("userInfo", mapResult.get("userInfo"));
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		mv.addObject("accessToken", request.getAttribute("accessToken"));
		mv.addObject("refreshToken", request.getAttribute("refreshToken"));
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * HR 담당자 정보 수정
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView userEdit(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			String msg = authUserService.updateAuthUserHR(paramMap);
			mv.addObject("message", msg);
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
	
		return mv;
	}
	
	/**
	 * 고객 삭제
	 * @param request
	 * @param paramMap
	 * @param repsonse
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/del")
	public ModelAndView userDel(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse repsonse) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			String msg = authUserService.deleteAuthUserHR(paramMap);
			mv.addObject("message", msg);
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 사용자 정보 조회
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/info")
	public ModelAndView userInfo(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			Map<String, Object> userInfo = authUserService.getAuthUser(paramMap);
			mv.addObject("resInfo", userInfo);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 비밀번호 체크
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/checkPswd")
	public ModelAndView userCheckPswd(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			String msg = authUserService.checkUserPassword(paramMap);
			mv.addObject("message", msg);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 비밀번호 변경
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/chgPswd")
	public ModelAndView userChgPswd(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			String msg = authUserService.changeUserPassword(paramMap);
			mv.addObject("message", msg);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * pushToken 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getPushToken")
	public ModelAndView getPushToken(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			List<String> list = authTokenService.getPushToken(paramMap);
			mv.addObject("message", "OK");
			mv.addObject("listener", list);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		 
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * HR담당자의 userKey 조회
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getUserKey")
	public ModelAndView getUserKey(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");

		try {
			List<Map<String, String>> list = authUserService.getUserKeyMsg(paramMap);
			mv.addObject("message", "OK");
			mv.addObject("list", list);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}

}
