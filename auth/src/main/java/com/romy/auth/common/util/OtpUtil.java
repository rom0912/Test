package com.romy.auth.common.util;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.romy.auth.common.logger.Log;
import com.romy.auth.module.service.AuthOtpService;

@Component
public class OtpUtil {

	@Autowired
	private AuthOtpService authOtpService;
	
	/**
	 * otp 생성
	 * @param userKey
	 * @return
	 */
	public int generateOTP(String userKey) {
		
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		
		authOtpService.saveAuthOtp(userKey, otp);
		
		return otp;
	}
	
	/**
	 * otp 값 조회
	 * @param userKey
	 * @return
	 */
	public int getOTP(String userKey) {
		try {
			return authOtpService.getAuthOtp(userKey);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			return 0;
		}
	}
	
	/**
	 * otp 만료 처리
	 * @param userKey
	 */
	public void clearOTP(String userKey) {
		try {
			authOtpService.deleteAuthOtp(userKey);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
	}
}
