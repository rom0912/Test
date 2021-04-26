package com.romy.auth.module.controller;

import java.util.HashMap;
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
import com.romy.auth.common.util.JwtTokenUtil;
import com.romy.auth.common.util.JwtTokenUtil.TOKEN_TYPE;
import com.romy.auth.module.service.AuthOtpService;
import com.romy.auth.module.service.AuthRsaService;
import com.romy.auth.module.service.AuthUserService;


@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthRsaService authRsaService;
	
	@Autowired
	private AuthUserService authUserService;
	
	@Autowired
	private AuthOtpService authOtpService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	

	/**
	 * RSA 키 발급
	 * @param request
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/encryption")
	public ModelAndView getRSAKey(HttpServletRequest request) throws Exception {
		
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		mv.addObject("resInfo", authRsaService.getRSAKey());
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 사용자 체크
	 * @param request
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/usercheck")
	public ModelAndView userCheck(HttpServletRequest request, @RequestBody Map<String, Object> paramMap)
			throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String, Object> mapResult = authUserService.checkUserByAdmin(paramMap);
		
		mv.addObject("resInfo", mapResult);
		Log.DebugEnd();
		
		return mv;
		
	}
	
	/**
	 * 토큰 발급
	 * @param request
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/token")
	public ModelAndView token(HttpServletRequest request, @RequestBody Map<String, Object> paramMap)
			throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String, Object> mapResult = authUserService.makeToken(paramMap);
		
		mv.addObject("resInfo", mapResult);
		Log.DebugEnd();
		
		return mv;
		
	}
	
	/**
	 * 토큰 재발급
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reToken")
	public ModelAndView reToken(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		String accessToken = "";
		String refreshToken = request.getHeader("refreshToken");
		if (refreshToken == null || "".equals(refreshToken))
			refreshToken = (String) paramMap.get("refreshToken");
		
		Boolean bChk = jwtTokenUtil.isTokenExpired(refreshToken, TOKEN_TYPE.REFRESH_TOKEN);
		if(!bChk) {
			Map<String, Object> mapToken = authUserService.makeReToken(refreshToken);
			accessToken = (String) mapToken.get("accessToken");
			refreshToken = (String) mapToken.get("refreshToken");
		}
		
		request.setAttribute("accessToken", accessToken);
		request.setAttribute("refreshToken", refreshToken);
		
		response.setHeader("accessToken", accessToken);
		response.setHeader("refreshToken", refreshToken);
		
		mapResult.put("accessToken", accessToken);
		mapResult.put("refreshToken", refreshToken);
		
		mv.addObject("resInfo", mapResult);
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 비밀번호 재설정 링크 발송
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/resetLink")
	public ModelAndView resetLink(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			Map<String, String> mapResult = authUserService.authUserPwResetLink(paramMap);
			mv.addObject("message", mapResult.get("message"));
			mv.addObject("email", mapResult.get("email"));
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
			mv.addObject("email", "");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * otp 체크
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/checkOtp")
	public ModelAndView checkOtp(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		Boolean bChk = authOtpService.checkAuthOtp(paramMap);
		mv.addObject("check", bChk);
		
		Log.DebugEnd();
		
		return mv;
	}
	
	
	/**
	 * 비밀번호 재설정
	 * @param request
	 * @param paramMap
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/resetPswd")
	public ModelAndView userResetPswd(HttpServletRequest request, @RequestBody Map<String, Object> paramMap,
			HttpServletResponse response) throws Exception {
	
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			String msg = authUserService.resetUserPassword(paramMap);
			mv.addObject("message", msg);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("message", "오류가 발생하였습니다.");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
	
	/**
	 * 로그아웃
	 * @param request
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/logout")
	public ModelAndView logout(HttpServletRequest request, @RequestBody Map<String, Object> paramMap)
			throws Exception {
		
		Log.DebugStart();
		
		ModelAndView mv = new ModelAndView("jsonView");
		
		try {
			authUserService.authUserLogout(paramMap);
			mv.addObject("msg", "OK");
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			mv.addObject("msg", "FAIL");
		}
		
		Log.DebugEnd();
		
		return mv;
	}
}
