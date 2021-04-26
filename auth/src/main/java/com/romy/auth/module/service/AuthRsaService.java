package com.romy.auth.module.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.common.logger.Log;
import com.romy.auth.common.util.RSA;
import com.romy.auth.entity.AuthRsa;
import com.romy.auth.repository.AuthRsaRepository;

@Service
public class AuthRsaService {

	@Autowired
	private AuthRsaRepository authRsaRepository;
	
	
	public Map<String, Object> getRSAKey() {
		
		Log.Debug();
		
		RSA rsa = RSA.getEncKey();
		
		String strSessionKey = RandomStringUtils.randomAlphanumeric(20);
		
		AuthRsa tbAuthRsa = new AuthRsa();
		tbAuthRsa.setSessionKey(strSessionKey);
		tbAuthRsa.setRsaKey(rsa.getPrivateKey());
		
		Map<String, Object> mapResult = new HashMap<>();
		
		if(authRsaRepository.save(tbAuthRsa) != null) {
			
			mapResult.put("publicKeyModulus", rsa.getPublicKeyModulus());
			mapResult.put("publicKeyExponent", rsa.getPublicKeyExponent());
			mapResult.put("sessionKey", strSessionKey);
		}
		
		return mapResult;
	}
	
	
	public String checkRSAKey(Map<String, Object> paramMap) throws Exception {
		
		String strSessionKey = (String) paramMap.get("sessionKey");
		Optional<AuthRsa> tbAuthRsa = authRsaRepository.findById(strSessionKey);
		if(tbAuthRsa.isPresent()) {
			return tbAuthRsa.get().getRsaKey();
		}
		
		return "";
	}
	
	public void deleteAuthSRSAKey(Map<String, Object> paramMap) throws Exception {
		String strSessionKey = (String) paramMap.get("sessionKey");
		
		authRsaRepository.deleteById(strSessionKey);
	}
	
}
