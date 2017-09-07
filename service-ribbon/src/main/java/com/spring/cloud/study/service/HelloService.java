package com.spring.cloud.study.service;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jeffrey
 * @since 2017/05/10 13:56
 */
@Service
public class HelloService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate defaultRestTemplate;

    @HystrixCommand(fallbackMethod = "hiError")
    public String hiService(String name) {
        return restTemplate.getForObject("http://eureka-client/hi?name=" + name, String.class);
    }

    @HystrixCommand(fallbackMethod = "hiError")
    @Suspendable
    public String hiRemoteService(String name) {
        return defaultRestTemplate
            .getForObject("http://localhost:8080/hi?name=" + name, String.class);
    }

    public String helloService() {
        return restTemplate.getForObject("http://sidecar/hello", String.class);
    }

    public String originHelloService() {
        return defaultRestTemplate.getForObject("http://localhost:8080/hello", String.class);
    }

    public String hiError(String name) {
        return "hi," + name + ",sorry,error!";
    }
}
