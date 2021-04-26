package com.romy.auth.module.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.common.util.CryptHash;
import com.romy.auth.common.util.RSA;
import com.romy.auth.entity.AuthUser;

@Service
public class MobileAuthService {

	@Autowired
	private AuthKeyService authKeyService;
	
	@Autowired
	private JwtAuthService userDetailService;
	
	@Autowired
	private AuthUserService authUserService;
	
	
	/**
	 * 사용자 체크 및 로그인
	 * @param paramMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> checkUserByMobile(Map<String, Object> paramMap, HttpServletRequest request)
			throws Exception {
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		String strUserId = (String) paramMap.get("id");
		String strPswd = (String) paramMap.get("pswd");
		String tenantKey = request.getHeader("tenantKey");
		
		// 복호화 처리
		String _uid = RSA.decrypt(strUserId);
		String _pwd = RSA.decrypt(strPswd);
		
		Map<String, String> mapCrypt = authKeyService.getCryptInfo();
		String strSalt = mapCrypt.get("salt");
		Integer intNum = Integer.parseInt(mapCrypt.get("interNum"));
		
		String hash = CryptHash.getHash(_pwd, strSalt, intNum);
		
		AuthUser user = userDetailService.authenticateByIdAndTenantKey(_uid, tenantKey);
		// 사용자가 없는 경우
		if(user == null) {
			mapResult.put("status", "FAIL");
			mapResult.put("message", "사용자가 없습니다.");
			return mapResult;
		}
		
		// 비밀번호가 일치하지 않은 경우
		if(!hash.equals(user.getPassword())) {
			mapResult.put("status", "FAIL");
			mapResult.put("message", "비밀번호가 일치하지 않습니다.");
			return mapResult;
		}
		
		mapResult.put("status", "SUCCESS");
		mapResult.put("message", "EXIST");
		mapResult.put("userKey", user.getUserKey());
		mapResult.put("uesrDivCd", user.getUserDivCd());
		mapResult.put("tenantKey", user.getTenantKey());
		mapResult.put("userName", user.getUserName());
		
		// 토큰 발급
		Map<String, Object> mapToken = authUserService.makeToken(mapResult);
		mapResult.putAll(mapToken);
		
		return mapResult;
	}
	
	/**
	 * 아이디 유효성 체크
	 * @param paramMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> checkUserIdByMobile(Map<String, Object> paramMap, HttpServletRequest request)
			throws Exception {
		
		Map<String, String> mapResult = new HashMap<String, String>();
		
		String userId = (String) paramMap.get("id");
		userId = RSA.decrypt(userId);
		String tenantKey = request.getHeader("tenantKey");
		
		AuthUser user = authUserService.getAuthUserIdAndTenantKey(userId, tenantKey);
		if(user == null) {
			mapResult.put("status", "SUCCESS");
			mapResult.put("message", "OK");
		} else {
			mapResult.put("status", "FAIL");
			mapResult.put("message", "이미 존재하는 아이디 입니다.");
		}
		
		return mapResult;
	}

}
