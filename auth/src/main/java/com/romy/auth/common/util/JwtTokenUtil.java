package com.romy.auth.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.romy.auth.common.logger.Log;
import com.romy.auth.module.service.AuthTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil {

	@Autowired
	private AuthTokenService authTokenService;
	
	
	@Value("${access.token.secure.key}")
	private String ACCESS_SECURE_KEY;
	
	@Value("${refresh.token.secure.key}")
	private String REFRESH_SECURE_KEY;
	
	@Value("${access.token.expire.time}")
	private Long ACCESS_EXPIRE_MINUTES;
	
	@Value("${refresh.token.expire.time}")
	private Long REFRESH_EXPIRE_MINUTES;
	
	
	/**
	 * 토큰 타입
	 * 
	 * @author SaeRomI
	 *
	 */
	public enum TOKEN_TYPE {
		ACCESS_TOKEN, REFRESH_TOKEN
	}
	
	
	/**
	 * 토큰 타입 데이터
	 * 
	 * @author SaeRomI
	 *
	 */
	private class TokenTypeData {
		
		private final String key;
		private final Long time;
		
		public TokenTypeData(String key, Long time) {
			super();
			this.key = key;
			this.time = time;
		}
		
		public String getKey() {
			return key;
		}
		
		public Long getTime() {
			return time;
		}
	}
	
	/**
	 * Jwt 객체
	 * @author SaeRomI
	 *
	 */
	public class Jwt {
		
		private final String accessToken;
		private final String refreshToken;
		
		public Jwt(String accessToken, String refreshToken) {
			super();
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
		}
		
		public String getAccessToken() {
			return accessToken;
		}
		
		public String getRefreshToken() {
			return refreshToken;
		}
	}
	
	
	/**
	 * 토큰 타입 데이터 생성
	 * @param tokenType	(필수) 토큰 타입
	 * @return
	 */
	private TokenTypeData makeTokenTypeData(TOKEN_TYPE tokenType) {
		
		String key = (tokenType == TOKEN_TYPE.ACCESS_TOKEN ? ACCESS_SECURE_KEY : REFRESH_SECURE_KEY);
		Long time = (tokenType == TOKEN_TYPE.ACCESS_TOKEN ? ACCESS_EXPIRE_MINUTES : REFRESH_EXPIRE_MINUTES);
		
		return new TokenTypeData(key, time);
	}
	
	/**
	 * 토큰의 만료시간을 추출한다.
	 * @param token		(필수) 토큰키
	 * @param tokenType	(필수) 토큰 타입 (ACCESS / REFRESH)
	 * @return
	 * @throws Exception
	 */
	private Date extExpireDate(String token, TOKEN_TYPE tokenType) throws Exception {
		return this.extClaim(token, Claims::getExpiration, tokenType);
	}
	
	/**
	 * 토큰 파싱
	 * @param token		(필수) 토큰키
	 * @param tokenData	(필수) 토큰 타입 데이터
	 * @return
	 * @throws Exception
	 */
	private Claims extAllClaims(String token, TokenTypeData tokenData) throws Exception {
		
		Claims body = null;
		
		try {
			body = Jwts.parser().setSigningKey(tokenData.getKey()).parseClaimsJws(token).getBody();
			
		} catch (ExpiredJwtException e) {
			Log.Debug("토큰 만료");
			
		} catch (UnsupportedJwtException e) {
			Log.Debug("형식이 일치하지 않음");
			
		} catch (MalformedJwtException e) {
			Log.Debug("JWT가 올바르게 구성되지 않음");
			
		} catch (SignatureException e) {
			Log.Debug("JWT Singature 오류");
			
		} catch (IllegalArgumentException e) {
			Log.Debug("부적절한 인수");
		}
		
		return body;
	}
	
	public <T> T extClaim(String token, Function<Claims, T> claimsResolver, TOKEN_TYPE tokenType) throws Exception {

		TokenTypeData tokenData = this.makeTokenTypeData(tokenType);
		Claims claims = extAllClaims(token, tokenData);
		
		return claimsResolver.apply(claims);
	}
	
	public String extUserKey(String token, TOKEN_TYPE tokenType) throws Exception {
		
		String sabun = "";
		
		try {
			sabun = this.extClaim(token, Claims::getId, tokenType); 
		} catch (Exception e) {
			sabun = "";
			Log.Debug(e.getMessage());
		}
		
		return sabun;
	}
	
	public Boolean validAccessToken(String token) throws Exception {
		String userKey = this.extUserKey(token, TOKEN_TYPE.ACCESS_TOKEN);
		
		return authTokenService.isValidAccessToken(userKey, token);
	}
	
	
	public String extTenantKey(String token, TOKEN_TYPE tokenType) throws Exception {
		
		TokenTypeData tokenData = this.makeTokenTypeData(tokenType);
		Claims claims = extAllClaims(token, tokenData);
		
		if(claims != null) {
			return (String) claims.get("tenantKey");
		}
		
		return "";
	}
	
	/**
	 * 토큰 만료 확인
	 * @param token		(필수) 토큰키
	 * @param tokenType	(필수) 토큰 타입 (ACCESS / REFRESH)
	 * @return
	 * @throws Exception
	 */
	public Boolean isTokenExpired(String token, TOKEN_TYPE tokenType) throws Exception {
		
		if(token == null || "null".equals(token)) return true;
		
		try {

			Date date = this.extExpireDate(token, tokenType);
			return date.before(new Date());
			
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			return true;
		}
	}
	
	/**
	 * 토큰 유효성 체크
	 * @param token
	 * @param user
	 * @param tokenType
	 * @return
	 */
	public Boolean validToken(String token, UserDetails userDetails, TOKEN_TYPE tokenType) {
		try {
			final String userKey = extUserKey(token, tokenType);
			return userKey.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenType);
		} catch (Exception e) {
			Log.Debug(e.getMessage());
			return false;
		}
	}
	
	/**
	 * 토큰 생성
	 * @param param
	 * @param subject
	 * @param tokenType
	 * @return
	 */
	private String createToken(Map<String, Object> param, String userKey, TOKEN_TYPE tokenType) {
		
		TokenTypeData tokenData = this.makeTokenTypeData(tokenType);
		LocalDateTime time = LocalDateTime.now().plusMinutes(tokenData.getTime());
		
		return Jwts.builder().setHeaderParam("typ", "JWT").setHeaderParam("alg", "HS256").setClaims(param)
				.setId(userKey).setExpiration(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()))
				.setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS256, tokenData.getKey()).compact();
	}
	
	/**
	 * 토큰생성 (ACCESS, REFRESH)
	 * @param param
	 * @return
	 */
	private Jwt generateToken(Map<String, Object> param) {
	
		String strUserKey = (String) param.get("userKey");
		
		String accessToken = this.createToken(param, strUserKey, TOKEN_TYPE.ACCESS_TOKEN);
		String refreshToken = this.createToken(new HashMap<String, Object>(), strUserKey, TOKEN_TYPE.REFRESH_TOKEN);
		
		return new Jwt(accessToken, refreshToken);
	}
	
	/**
	 * oAuth 토큰 발행
	 * @param param
	 * @return
	 */
	public Jwt makeJwt(Map<String, Object> param) throws Exception {
		
		return this.generateToken(param);
	}
	
	/**
	 * AccessToken 재발행
	 * @param param
	 * @param refreshToken
	 * @return
	 * @throws Exception
	 */
	public Jwt makeReJwt(Map<String, Object> param, String refreshToken) throws Exception {
		String strUserKey = (String) param.get("userKey");
		
		String accessToken = this.createToken(param, strUserKey, TOKEN_TYPE.ACCESS_TOKEN);
		
		return new Jwt(accessToken, refreshToken); 
	}
	
	
}
