package com.spring.cloud.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ServiceWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceWebsocketApplication.class, args);
	}
}
