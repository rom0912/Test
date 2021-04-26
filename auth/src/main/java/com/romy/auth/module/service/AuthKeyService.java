package com.romy.auth.module.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.entity.AuthKey;
import com.romy.auth.repository.AuthKeyRepository;

@Service
public class AuthKeyService {

	@Autowired
	private AuthKeyRepository authKeyRepository;
	
	/**
	 * 암호화 정보 조회
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getCryptInfo() throws Exception {
	
		Map<String, String> mapResult = new HashMap<>();
		
		Optional<AuthKey> optSalt = authKeyRepository.findById("S");
		if(optSalt.isPresent()) {
			mapResult.put("salt", optSalt.get().getKeyValue());
		}
		
		Optional<AuthKey> optInterNum = authKeyRepository.findById("I");
		if(optInterNum.isPresent()) {
			mapResult.put("interNum", optInterNum.get().getKeyValue());
		}
			
		return mapResult;
	}
	
}
