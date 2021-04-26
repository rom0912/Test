package com.romy.auth.module.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.entity.AuthToken;
import com.romy.auth.repository.AuthTokenRepository;

@Service
public class AuthTokenService {

	@Autowired
	private AuthTokenRepository authTokenRepository;
	
	

	/**
	 * 토근정보 수정
	 * @param userKey
	 * @param accessToken
	 * @param refreshToken
	 * @throws Exception
	 */
	public void updAuthTokenByWeb(String userKey, String accessToken, String refreshToken) throws Exception {
		
		AuthToken token = authTokenRepository.findByUserKey(userKey);
		if(token == null) {
			token = new AuthToken();
			token.setUserKey(userKey);
		}
		
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		
		authTokenRepository.save(token);
	}
	
	/**
	 * 중복로그인 및 유효한 리프레시 토큰인지 체크
	 * @param userKey
	 * @param refreshToken
	 * @return
	 * @throws Exception
	 */
	public Boolean isExistAuthToken(String userKey, String refreshToken) throws Exception {
		
		AuthToken token = authTokenRepository.findByUserKey(userKey);
		if(token != null) {
			String orgRefreshToken = token.getRefreshToken(); 
			if( orgRefreshToken == null || "".equals(orgRefreshToken) || refreshToken.equals(orgRefreshToken) ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * AccessToken 체크
	 * @param userKey
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	public Boolean isValidAccessToken(String userKey, String accessToken) throws Exception {
		
		AuthToken token = authTokenRepository.findByUserKey(userKey);
		if(token != null) {
			String orgAccessToken = token.getAccessToken();
			if( orgAccessToken != null && orgAccessToken.equals(accessToken) ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * push 토큰 셋팅
	 * @param paramMap
	 * @throws Exception
	 */
	public List<String> getPushToken(Map<String, Object> paramMap) throws Exception {
		
		List<String> list = (List<String>) paramMap.get("listener");
		
		for (Integer i=0, size=list.size(); i < size; i++) {
			String userKey = list.get(i);
			AuthToken token = authTokenRepository.findByUserKey(userKey);
			list.set(i, token.getPushToken());
		}
		
		return list;
	}
	
}
