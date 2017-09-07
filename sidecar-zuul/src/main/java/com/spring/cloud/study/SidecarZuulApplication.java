package com.spring.cloud.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableSidecar
@EnableHystrixDashboard
@EnableTurbine
@SpringBootApplication
public class SidecarZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(SidecarZuulApplication.class, args);
    }
}
