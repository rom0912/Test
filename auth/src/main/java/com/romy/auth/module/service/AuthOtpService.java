package com.romy.auth.module.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.common.util.RSA;
import com.romy.auth.entity.AuthOtp;
import com.romy.auth.repository.AuthOtpRepository;

@Service
public class AuthOtpService {

	@Autowired
	private AuthOtpRepository authOtpRepository;

	/**
	 * otp 저장
	 * @param userKey
	 * @param otp
	 */
	public void saveAuthOtp(String userKey, Integer otp) {
		AuthOtp authOtp = authOtpRepository.findByUserKey(userKey);
		if(authOtp == null) {
			authOtp = new AuthOtp();
			authOtp.setUserKey(userKey);
		}
		authOtp.setOtp(String.valueOf(otp));
		authOtpRepository.save(authOtp);
	}
	
	/**
	 * otp 조회
	 * @param userKey
	 * @return
	 */
	public Integer getAuthOtp(String userKey) {
		AuthOtp authOtp = authOtpRepository.findByUserKey(userKey);
		return Integer.parseInt(authOtp.getOtp());
	}
	
	/**
	 * otp 삭제
	 * @param userKey
	 */
	public void deleteAuthOtp(String userKey) {
		AuthOtp authOtp = authOtpRepository.findByUserKey(userKey);
		authOtpRepository.delete(authOtp);
	}
	
	/**
	 * otp 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Boolean checkAuthOtp(Map<String, Object> paramMap) throws Exception {
		
		LocalDateTime curDate = LocalDateTime.now();
		
		String userKey = (String) paramMap.get("userKey");
		userKey = RSA.decrypt(userKey);
		String otp = (String) paramMap.get("otp");
		otp = RSA.decrypt(otp);
		
		AuthOtp authOtp = authOtpRepository.findByUserKeyAndOtp(userKey, otp);
		if(authOtp != null) {
			LocalDateTime crtDate = authOtp.getCreateDate();
			crtDate.plusDays(1);
			
			// 체크한 후 삭제
			this.deleteAuthOtp(userKey);
			
			return crtDate.isAfter(curDate);
		}
		
		return false;
		
	}
	
}
