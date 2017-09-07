package com.spring.cloud.study.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jeffrey
 * @since 2017/05/19 15:31
 */
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    String servicePort;

    @Value("${spring.application.name}")
    String serviceName;

    @Value("${server.address}")
    String serviceAddress;

    @RequestMapping("/test1")
    public String test1() {
        return "I'm " + serviceName + ", from " + serviceAddress + ":" + servicePort;
    }

    @RequestMapping("/test2")
    public String test2() {
        return restTemplate.getForObject("http://consul-client-test/test1", String.class);
    }
}
