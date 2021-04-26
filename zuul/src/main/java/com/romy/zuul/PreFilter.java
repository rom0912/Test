package com.romy.zuul;

import javax.servlet.http.Cookie;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class PreFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		
		Cookie[] cookie = ctx.getRequest().getCookies();
		if (cookie != null) {
			for (Cookie coo : cookie) {
				ctx.addZuulRequestHeader(coo.getName(), coo.getValue());
			}
		}
		
	     return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return FilterConstants.SEND_RESPONSE_FILTER_ORDER;
	}
	
}
