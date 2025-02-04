package com.backend.babyspa.v1.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2); // Minimalan broj aktivnih thread-ova
		executor.setMaxPoolSize(5); // Maksimalan broj thread-ova
		executor.setQueueCapacity(50); // Kapacitet reda čekanja
		executor.initialize();
		return executor;
	}

}
