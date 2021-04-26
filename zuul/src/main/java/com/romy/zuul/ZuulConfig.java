package com.romy.zuul;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZuulConfig {

	@Bean
	public PreFilter preFilter() {
		return new PreFilter();
	}
	
}
