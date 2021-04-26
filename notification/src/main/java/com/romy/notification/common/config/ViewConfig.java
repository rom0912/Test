package com.romy.notification.common.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
public class ViewConfig {

	@Bean
	MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
	
	@Bean(name = "asyncTask")
	public TaskExecutor asyncTask() {

	   ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	   executor.setThreadNamePrefix("asyncTask-");
	   executor.setCorePoolSize(5);
	   executor.setMaxPoolSize(100);
	   executor.setQueueCapacity(0);
	   executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
	   return executor;
	}
	
}
