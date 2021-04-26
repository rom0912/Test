package com.romy.auth.common.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.romy.auth.common.logger.Log;
import com.romy.auth.common.util.JwtTokenUtil;
import com.romy.auth.common.util.JwtTokenUtil.TOKEN_TYPE;
import com.romy.auth.module.service.AuthUserService;
import com.romy.auth.module.service.JwtAuthService;

import io.jsonwebtoken.ExpiredJwtException;



@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private JwtAuthService jwtAuthService;
	
	@Autowired
	private AuthUserService authUserService;
	
	
	private static final List<String> EXCLUDE_URL = Collections.unmodifiableList((Arrays.asList("/auth/encryption",
			"/auth/usercheck", "/auth/token", "/auth/reToken", "/auth/resetPswd", "/auth/resetLink",
			"/auth/checkOtp", "/auth/mobile/*", "/auth/user/getUserKey")));
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String reqTokenHeader = request.getHeader("Authorization");
		
		String userKey = null;
		String accessToken = null;
		
		if (reqTokenHeader != null && reqTokenHeader.startsWith("Bearer")) {
			accessToken = reqTokenHeader.substring(7);
		} else {
			accessToken = request.getHeader("accessToken");	
		}
		
		try {
			String refreshToken = request.getHeader("refreshToken");
			Boolean bChk = jwtTokenUtil.isTokenExpired(accessToken, TOKEN_TYPE.ACCESS_TOKEN);
			if(bChk) {
				
				bChk = jwtTokenUtil.isTokenExpired(refreshToken, TOKEN_TYPE.REFRESH_TOKEN);
				if(!bChk) {
					Map<String, Object> mapToken = authUserService.makeReToken(refreshToken);
					accessToken = (String) mapToken.get("accessToken");
					refreshToken = (String) mapToken.get("refreshToken");
					
					request.setAttribute("accessToken", accessToken);
					request.setAttribute("refreshToken", refreshToken);
					
					response.setHeader("accessToken", accessToken);
					response.setHeader("refreshToken", refreshToken);
				}
			} else {
				/*
				bChk = jwtTokenUtil.validAccessToken(accessToken);
				if(!bChk) accessToken = "";
				*/
				request.setAttribute("accessToken", accessToken);
				request.setAttribute("refreshToken", refreshToken);
				
				response.setHeader("accessToken", accessToken);
				response.setHeader("refreshToken", refreshToken);
			}
			
			try {
				userKey = jwtTokenUtil.extUserKey(accessToken, TOKEN_TYPE.ACCESS_TOKEN);
			} catch (IllegalArgumentException e) {
				Log.Debug("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				Log.Debug("JWT Token has expired");
			} catch (Exception e) {
				Log.Debug(e.getMessage());
			}
		} catch (Exception e) {
			Log.Debug(e.getMessage());
		}
		
		if(userKey != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		
			UserDetails userDetails = this.jwtAuthService.loadUserByUsername(userKey);
			
			if(jwtTokenUtil.validToken(accessToken, userDetails, TOKEN_TYPE.ACCESS_TOKEN)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
	}
	
}
