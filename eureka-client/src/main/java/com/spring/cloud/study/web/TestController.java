package com.spring.cloud.study.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jeffrey
 * @since 2017/05/23 13:47
 */
@RestController
public class TestController {

    @Value("${server.port}")
    private String port;

    @Value("${foo:default_foo}")
    private String foo;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/hi")
    @HystrixCommand()
    public String home(@RequestParam String name) {
        return "hi " + name + ",i am from port:" + port;
    }

    @GetMapping("/foo")
    public String hi() {
        return foo;
    }

    @GetMapping("/exception")
    public String exception(){
        throw new RuntimeException("error!!!");
    }

    @GetMapping("/half")
    public String halfsec() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);
        return "success";
    }

    @GetMapping("/timeout")
    public String timeout() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return "success";
    }

    @GetMapping("/instances")
    public List<ServiceInstance> getInstances(@RequestParam String name) {
        return discoveryClient.getInstances(name);
    }

    @GetMapping("/services")
    public List<String> getServices() {
        return discoveryClient.getServices();
    }
}
