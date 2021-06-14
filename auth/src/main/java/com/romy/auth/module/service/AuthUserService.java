package com.romy.auth.module.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.romy.auth.common.logger.Log;
import com.romy.auth.common.util.CryptHash;
import com.romy.auth.common.util.JwtTokenUtil;
import com.romy.auth.common.util.JwtTokenUtil.Jwt;
import com.romy.auth.common.util.JwtTokenUtil.TOKEN_TYPE;
import com.romy.auth.common.util.OtpUtil;
import com.romy.auth.common.util.RSA;
import com.romy.auth.common.util.RabbitMQUtil;
import com.romy.auth.entity.AuthTenant;
import com.romy.auth.entity.AuthUser;
import com.romy.auth.repository.AuthUserRepository;

@Service
public class AuthUserService {

	@Autowired
	private AuthKeyService authKeyService;
	
	@Autowired
	private AuthRsaService authRsaService;
	
	@Autowired
	private AuthTokenService authTokenService;
	
	@Autowired
	private AuthTenantService authTenantService;
	
	@Autowired
	private JwtAuthService userDetailService;
	
	@Autowired
	private AuthUserRepository authUserRepository;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private OtpUtil otpUtil;
	
	@Autowired
	private RabbitMQUtil rabbitMQUtil;
	
	
	private static final String EMAIL_PATTERN = "([\\w.])(?:[\\w.]*)(@.*)";
	
	/**
	 * HR 담당자 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectAuthHRUser(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		List tenantList = (ArrayList) paramMap.get("tenantKeyList");
		
		if(tenantList != null) {
			
			try {
				List<AuthUser> userList = authUserRepository.findByUserDivCdAndTenantKeyIn("H", tenantList);
				
				DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd");
				
				for (AuthUser user : userList) {
					Map<String, Object> mapUser = new HashMap<>();
					
					AuthTenant tenant = authTenantService.getAuthTenantInfoByTenantKey(user.getTenantKey());
					
					if(tenant == null) continue;
					
					mapUser.put("tenantId", tenant.getTenantId());
					mapUser.put("tenantKey", tenant.getTenantKey());
					mapUser.put("tenantName", tenant.getTenantName());
					mapUser.put("userName", user.getUserName());
					mapUser.put("note", user.getNote());
					mapUser.put("email", user.getEmail());
					
					String phoneNo = user.getPhoneNo();
					if(phoneNo != null) {
						if(phoneNo.length() == 11) {
							phoneNo = phoneNo.substring(0, 3) + "-" + phoneNo.substring(3, 7) + "-" + phoneNo.substring(7);  
						} else if(phoneNo.length() == 10) {
							phoneNo = phoneNo.substring(0, 3) + "-" + phoneNo.substring(3, 6) + "-" + phoneNo.substring(6);
						}
					}
					
					mapUser.put("phoneNo", phoneNo);
					mapUser.put("joinYmd", format.format(user.getJoinYmd()));
					
					list.add(mapUser);
				}
			} catch (Exception e) {
				Log.Debug(e.getMessage());
			}
		}
		
		return list;
	}
	
	
	/**
	 * 유저 추가
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> insertAuthUser(Map<String, Object> paramMap) throws Exception {
		
		Log.Debug();
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		RSA rsa = RSA.getEncKey();
		
		// RSA 키 조회
		String strKey = authRsaService.checkRSAKey(paramMap);
		if(!"".equals(strKey)) {
			
			String userKey = RandomStringUtils.randomAlphanumeric(50);
			String strUserId = (String) paramMap.get("email");
			String strPswd = (String) paramMap.get("password");
			
			// 복호화 처리
			String _uid = rsa.decryptRsa(strKey, strUserId);
			String _pwd = rsa.decryptRsa(strKey, strPswd);
			
			Map<String, String> mapCrypt = authKeyService.getCryptInfo();
			String strSalt = mapCrypt.get("salt");
			Integer intNum = Integer.parseInt(mapCrypt.get("interNum"));
			
			String hash = CryptHash.getHash(_pwd, strSalt, intNum);
			
			String userDivCd = (String) paramMap.get("userDivCd");
			String tenantName = (String) paramMap.get("tenantName");
			String tenantKey = (String) paramMap.get("tenantKey");
			String userName = (String) paramMap.get("userName");
			String note = (String) paramMap.get("note");
			String email = (String) paramMap.get("email");
			String phoneNo = (String) paramMap.get("phoneNo");
			String joinYmd = (String) paramMap.get("joinYmd");
			Object address = (Object) paramMap.get("address");
			
			// 아이디 중복 체크
			AuthUser user = authUserRepository.findByUserIdAndTenantKey(_uid, tenantKey);
			if(user == null) {
				// 테넌트 생성
				AuthTenant tenant = authTenantService.saveAuthTenant(paramMap);
				
				if(tenant != null) {
					Map<String, Object> tenantInfo = new HashMap<String, Object>();
					tenantInfo.put("tenantId", tenant.getTenantId());
					tenantInfo.put("tenantKey", tenant.getTenantKey());
					tenantInfo.put("tenantName", tenant.getTenantName());
					
					user = new AuthUser();
					
					user.setUserKey(userKey);
					user.setUserId(_uid);
					user.setPassword(hash);
					user.setUserName(userName);
					if(note != null && !"".equals(note)) {
						user.setNote(note);
					}
					
					// HR담당자인 경우
					if("H".equals(userDivCd)) {
						user.setInitLoginYn("Y");
					} else {
						user.setInitLoginYn("N");
					}
					
					user.setRockingYn("N");
					user.setUserDivCd(userDivCd);
					
					if(joinYmd == null || "".equals(joinYmd)) {
						user.setJoinYmd(LocalDate.now());
					} else {
						DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
						user.setJoinYmd(LocalDate.parse(joinYmd, format));
					}
					
					if(email != null && !"".equals(email)) {
						user.setEmail(email);
					}
					
					if(!"U".equals(userDivCd)) {
						user.setEmail(_uid);
					}
					
					if(phoneNo != null && !"".equals(phoneNo)) {
						user.setPhoneNo(phoneNo);
					}
					
					if(address != null) {
						user.setAddress(address);
					}
					user.setTenantKey(tenantKey);
					
					user = authUserRepository.save(user);
					
					Map<String, Object> userInfo = new HashMap<String, Object>();
					userInfo.put("userKey", user.getUserKey());
					userInfo.put("userName", user.getUserName());
					userInfo.put("userDivCd", user.getUserDivCd());
					userInfo.put("email", user.getEmail());
					userInfo.put("phoneNo", user.getPhoneNo());
					userInfo.put("address", user.getAddress());
					
					mapResult.put("msg", "OK");
					mapResult.put("tenantInfo", tenantInfo);
					mapResult.put("userInfo", userInfo);
					
					authRsaService.deleteAuthSRSAKey(paramMap);
					
				} else {
					mapResult.put("msg", "FAIL");
				}
			} else {
				mapResult.put("msg", "ISEXIST");
				
				return mapResult;
			}
		} else {
			mapResult.put("msg", "FAIL");
		}
		
		return mapResult;
	}
	
	/**
	 * HR 고객정보 수정
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String updateAuthUserHR(Map<String, Object> paramMap) throws Exception {
		
		String tenantKey = (String) paramMap.get("tenantKey");
		
		String msg = "고객정보가 없습니다.";
		
		// HR담당자 정보를 수정한다.
		AuthUser user = authUserRepository.findByUserDivCdAndTenantKey("H", tenantKey);
		if(user != null) {
		
			String userName = (String) paramMap.get("userName");
			String note = (String) paramMap.get("note");
			String email = (String) paramMap.get("email");
			String phoneNo = (String) paramMap.get("phoneNo");
			
			user.setUserName(userName);
			user.setNote(note);
			user.setUserId(email);
			user.setEmail(email);
			user.setPhoneNo(phoneNo);
			
			authUserRepository.save(user);
			msg = "OK";
		}
		return msg;
	}
	
	/**
	 * 테넌트키에 해당하는 사용자 삭제 (테넌트도 삭제)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String deleteAuthUserHR(Map<String, Object> paramMap) throws Exception {
		
		String tenantKey = (String) paramMap.get("tenantKey");
		
		String msg = "고객정보가 없습니다.";
		
		// 사용자 삭제
		List<AuthUser> userList = authUserRepository.findByTenantKey(tenantKey);
		if(userList != null && userList.size() > 0) {
			authUserRepository.deleteAll(userList);
			msg = "OK";
		}
		
		// 테넌트 삭제
		AuthTenant tenant = authTenantService.getAuthTenantInfoByTenantKey(tenantKey);
		if(tenant != null) {
			authTenantService.deleteAuthTenantByTenantKey(tenant);
		}
		
		return msg;
	}
	
	/**
	 * 유저 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAuthUser(Map<String, Object> paramMap) throws Exception {
		
		String userKey = (String) paramMap.get("userKey");
		String tenantKey = (String) paramMap.get("tenantKey");
		
		AuthUser user = authUserRepository.findByUserKeyAndTenantKey(userKey, tenantKey);
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		if(user != null) {
			mapResult.put("userKey", user.getUserKey());
			mapResult.put("tenantKey", user.getTenantKey());
			mapResult.put("id", user.getUserId());
			mapResult.put("password", user.getPassword());
			mapResult.put("userDivCd", user.getUserDivCd());
			mapResult.put("userName", user.getUserName());
			mapResult.put("email", user.getEmail());
			mapResult.put("phoneNo", user.getPhoneNo());
			mapResult.put("msg", "SUCCESS");
			
		} else {
			mapResult.put("userKey", "");
			mapResult.put("tenantKey", "");
			mapResult.put("id", "");
			mapResult.put("password", "");
			mapResult.put("userDivCd", "");
			mapResult.put("userName", "");
			mapResult.put("email", "");
			mapResult.put("phoneNo", "");
			mapResult.put("msg", "NOTEXIST");
		}
		
		return mapResult;
	}
	
	/**
	 * 사용자 체크 (관리자)
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> checkUserByAdmin(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> mapResult = new HashMap<>();
		
		RSA rsa = RSA.getEncKey();
		
		// RSA 키 조회
		String strKey = authRsaService.checkRSAKey(paramMap);
		if(!"".equals(strKey)) {
			String strUserId = (String) paramMap.get("id");
			String strPswd = (String) paramMap.get("pswd");
			//String tenantKey = (String) paramMap.get("tenantKey");
			
			// 복호화 처리
			String _uid = rsa.decryptRsa(strKey, strUserId);
			String _pwd = rsa.decryptRsa(strKey, strPswd);
			
			Map<String, String> mapCrypt = authKeyService.getCryptInfo();
			String strSalt = mapCrypt.get("salt");
			Integer intNum = Integer.parseInt(mapCrypt.get("interNum"));
			
			String hash = CryptHash.getHash(_pwd, strSalt, intNum);
			
			AuthUser user = userDetailService.authenticateByIdAndUserDivCd(_uid, "A");
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
			//mapResult.put("userId", user.getUserId());
			mapResult.put("initLoginYn", user.getInitLoginYn());
			mapResult.put("userName", user.getUserName());
			
			authRsaService.deleteAuthSRSAKey(paramMap);
			
		} else {
			// 세션에 해당하는 암호화키가 없는 경우
			mapResult.put("status", "FAIL");
			mapResult.put("message", "잘못된 접근입니다.");
			return mapResult;
		}
		
		return mapResult;
	}
	
	/**
	 * 토큰 발급
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> makeToken(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		String tenantKey = (String) paramMap.get("tenantKey");
		String userKey = (String) paramMap.get("userKey");
			
		AuthUser user = authUserRepository.findByUserKeyAndTenantKey(userKey, tenantKey);
		if(user != null) {
			Map<String, Object> mapJwt = new HashMap<String, Object>();
			
			mapJwt.put("userKey", user.getUserKey());
			mapJwt.put("userId", user.getUserId());
			mapJwt.put("userDivCd", user.getUserDivCd());
			mapJwt.put("tenantKey", user.getTenantKey());
			
			Jwt jwt = jwtTokenUtil.makeJwt(paramMap);
			
			if(jwt != null) {
				
				authTokenService.updAuthTokenByWeb(user.getUserKey(), jwt.getAccessToken(), jwt.getRefreshToken());
				
				mapResult.put("accessToken", jwt.getAccessToken());
				mapResult.put("refreshToken", jwt.getRefreshToken());
				
				paramMap.put("userName", user.getUserName());
				rabbitMQUtil.sendLogLogin(paramMap);
			} else {
				mapResult.put("accessToken", "");
				mapResult.put("refreshToken", "");
			}
		} else {
			mapResult.put("accessToken", "");
			mapResult.put("refreshToken", "");
		}
	
		return mapResult;
	}
	
	/**
	 * 토큰 재발급
	 * @param refreshToken
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> makeReToken(String refreshToken) throws Exception {
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		String userKey = jwtTokenUtil.extUserKey(refreshToken, TOKEN_TYPE.REFRESH_TOKEN);
		Boolean bChk = authTokenService.isExistAuthToken(userKey, refreshToken);
		if(bChk) {
			AuthUser user = authUserRepository.findByUserKey(userKey);
			
			mapResult.put("userKey", user.getUserKey());
			mapResult.put("userId", user.getUserId());
			mapResult.put("userDivCd", user.getUserDivCd());
			mapResult.put("tenantKey", user.getTenantKey());
			
			Jwt jwt = jwtTokenUtil.makeReJwt(mapResult, refreshToken);
			
			if(jwt != null) {
				authTokenService.updAuthTokenByWeb(user.getUserKey(), jwt.getAccessToken(), jwt.getRefreshToken());
				mapResult.put("accessToken", jwt.getAccessToken());
				mapResult.put("refreshToken", jwt.getRefreshToken());
			} else {
				mapResult.put("accessToken", "");
				mapResult.put("refreshToken", "");
			}
		} else {
			mapResult.put("accessToken", "");
			mapResult.put("refreshToken", "");
		}
	
		return mapResult;
	}
	
	/**
	 * 비밀번호 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String checkUserPassword(Map<String, Object> paramMap) throws Exception {
		
		String msg = "오류가 발생하였습니다.";
		
		RSA rsa = RSA.getEncKey();
		
		// RSA 키 조회
		String strKey = authRsaService.checkRSAKey(paramMap);
		if(!"".equals(strKey)) {
			String password = (String) paramMap.get("curntPassword");
			// 복호화 처리
			String _pwd = rsa.decryptRsa(strKey, password);
			
			Map<String, String> mapCrypt = authKeyService.getCryptInfo();
			String strSalt = mapCrypt.get("salt");
			Integer intNum = Integer.parseInt(mapCrypt.get("interNum"));
			
			String hash = CryptHash.getHash(_pwd, strSalt, intNum);
			
			String tenantKey = (String) paramMap.get("tenantKey");
			String userKey = (String) paramMap.get("userKey");
			
			AuthUser user = authUserRepository.findByUserKeyAndTenantKey(userKey, tenantKey);
			if(user != null) {
				if(!hash.equals(user.getPassword())) {
					msg = "현재 비밀번호와 일치하지 않습니다.";
				} else {
					msg = "OK";
				}
			}
		}
		
		return msg;
	}
	
	/**
	 * 비밀번호 변경
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String changeUserPassword(Map<String, Object> paramMap) throws Exception {
		
		String msg = "오류가 발생하였습니다.";
		
		RSA rsa = RSA.getEncKey();
		
		// RSA 키 조회
		String strKey = authRsaService.checkRSAKey(paramMap);
		if(!"".equals(strKey)) {
			
			String newPassword = (String) paramMap.get("newPassword");
			
			// 복호화 처리
			String _newPwd = rsa.decryptRsa(strKey, newPassword);
			
			Map<String, String> mapCrypt = authKeyService.getCryptInfo();
			String strSalt = mapCrypt.get("salt");
			Integer intNum = Integer.parseInt(mapCrypt.get("interNum"));
			
			String hash = CryptHash.getHash(_newPwd, strSalt, intNum);
			
			String tenantKey = (String) paramMap.get("tenantKey");
			String userKey = (String) paramMap.get("userKey");
			
			AuthUser user = authUserRepository.findByUserKeyAndTenantKey(userKey, tenantKey);
			if(user != null) {
				user.setPassword(hash);
				authUserRepository.save(user);
				
				authRsaService.deleteAuthSRSAKey(paramMap);
				msg = "OK";
			}
		}
		
		return msg;
	}
	
	/**
	 * 비밀번호 재설정
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String resetUserPassword(Map<String, Object> paramMap) throws Exception {
		
		String msg = "오류가 발생하였습니다.";
		
		RSA rsa = RSA.getEncKey();
		
		// RSA 키 조회
		String strKey = authRsaService.checkRSAKey(paramMap);
		if(!"".equals(strKey)) {
			
			String newPassword = (String) paramMap.get("newPassword");
			
			// 복호화 처리
			String _newPwd = rsa.decryptRsa(strKey, newPassword);
			
			Map<String, String> mapCrypt = authKeyService.getCryptInfo();
			String strSalt = mapCrypt.get("salt");
			Integer intNum = Integer.parseInt(mapCrypt.get("interNum"));
			
			String hash = CryptHash.getHash(_newPwd, strSalt, intNum);
			
			String tenantKey = (String) paramMap.get("tenantKey");
			String userKey = (String) paramMap.get("userKey");
			
			AuthUser user = authUserRepository.findByUserKey(userKey);
			if(user != null) {
				
				if(hash.equals(user.getPassword())) {
					msg = "현재 비밀번호와 동일하게 변경할 수 없습니다.";
				} else {
					user.setPassword(hash);
					authUserRepository.save(user);
					
					authRsaService.deleteAuthSRSAKey(paramMap);
					msg = "OK";
				}
			}
		}
		
		return msg;
	}
	
	/**
	 * logout
	 * @param paramMap
	 * @throws Exception
	 */
	public void authUserLogout(Map<String, Object> paramMap) throws Exception {
		
		String userKey = (String) paramMap.get("userKey");
		
		authTokenService.updAuthTokenByWeb(userKey, "", "");
	}
	
	/**
	 * 비밀번호 재설정
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> authUserPwResetLink(Map<String, Object> paramMap) throws Exception {
		
		String msg = "오류가 발생하였습니다.";
		
		Map<String, String> mapResult = new HashMap<String, String>();
		
		String userId = (String) paramMap.get("userId");
		String userDivCd = (String) paramMap.get("userDivCd");
		
		AuthUser user = authUserRepository.findByUserIdAndUserDivCd(userId, userDivCd);
		if(user == null) {
			msg = "가입되지 않은 아이디(이메일) 입니다.";
			mapResult.put("message", msg);
			mapResult.put("email", "");
			
			return mapResult;
		}
		
		// OTP생성
		int otp = otpUtil.generateOTP(user.getUserKey());
		String encryptOtp = RSA.encrypt(String.valueOf(otp));
		String encryptUserKey = RSA.encrypt(user.getUserKey());
		
		// 모듈명
		String moduleNm = (String) paramMap.get("moduleNm");
		// 모듈
		String module = (String) paramMap.get("module");
		// endPoint
		String endPoint = (String) paramMap.get("endPoint");
		endPoint = RSA.decrypt(endPoint);
		
		// 수신자 이메일
		String email = user.getEmail();
		
		StringBuilder sbText = new StringBuilder();
		sbText.append("안녕하세요!<br>사용자님의 '").append(moduleNm).append("' 계정에 대한 <b>비밀번호 재설정이 요청</b> 되었습니다.")
				.append("<br><br><br><form action='").append(endPoint).append("' method='POST' target='_blank'>")
				.append("<input type='hidden' name='userKey' value='").append(encryptUserKey).append("'/>")
				.append("<input type='hidden' name='otp' value='").append(encryptOtp).append("'/>")
				.append("<button type='submit' style='width:140px; padding: 6px 0px; border-radius: 6px; color:#ffffff; background-color: #384757; border: solid 1px #2b3643; font-size: 12px;'><b>비밀번호 재설정</b></button></form>")
				.append("<br><br>비밀번호를 변경하고 싶지 않거나 본인이 요청한 것이 아닌 경우, 본 메일을 <b>삭제</b>하시기 바랍니다.")
				.append("<br><br>감사합니다.<br><br>").append(moduleNm).append("팀 드림.");
		
		String title = "["+ moduleNm +"] 비밀번호 재설정";
		
		Map<String, String> mapMsg = new HashMap<String, String>();
		mapMsg.put("msgTypeCd", "M");
		mapMsg.put("title", title);
		mapMsg.put("message", sbText.toString());
		mapMsg.put("module", module);
		
		List<String> receiveList = new ArrayList<String>();
		receiveList.add(email);
		
		rabbitMQUtil.sendNotification(mapMsg, receiveList, null, null);
		
		msg = "OK";
		mapResult.put("message", msg);
		mapResult.put("email", email.replaceAll(EMAIL_PATTERN, "$1****$2"));
		
		return mapResult;
	}
	
	/**
	 * 사용자 조회 (ID 중복확인 용)
	 * @param userId
	 * @param tenantKey
	 * @return
	 */
	public AuthUser getAuthUserIdAndTenantKey(String userId, String tenantKey) {
		
		return authUserRepository.findByUserIdAndTenantKey(userId, tenantKey);
	}
	
	/**
	 * userKey 리스트를 조회한다.
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getUserKeyMsg(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		
		List<Map<String, String>> dataList = (List<Map<String,String>>) paramMap.get("list");
		for (Map<String, String> data : dataList) {
			String tenantKey = data.get("tenantKey");
			AuthUser user = authUserRepository.findByUserDivCdAndTenantKey("H", tenantKey);
			if(user != null) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userKey", user.getUserKey());
				map.put("msgInfo", data.get("msgInfo"));
				resultList.add(map);
			}
		}
		
		return resultList;
	}
	
}
