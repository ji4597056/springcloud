package com.spring.cloud.study.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jeffrey
 * @since 2017/05/22 13:36
 */
@RestController
public class HelloController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(name = "/hello", method = RequestMethod.GET)
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("response", null);
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "");
        result.put("resource_type", "test");
        result.put("resource_id", "123");
        return result;
    }

    @RequestMapping(name = "/hello", method = RequestMethod.HEAD)
    public void hello1(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(403);
    }

    @RequestMapping(name = "/test")
    public String test(){
       return "I am spring boot!";
    }

    @GetMapping("/reHello")
    public String reHello() {
        return "reHello,I'm spring-boot.";
    }

    @GetMapping("/timeout")
    public String timeout() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500L);
        return "success";
    }

    @GetMapping("/exception")
    public String exception(HttpServletResponse response) throws InterruptedException {
        response.setStatus(500);
        TimeUnit.SECONDS.sleep(2);
        return "exception";
    }

    @GetMapping("/proxy/foo")
    public String fooProxy() {
        return restTemplate.getForObject("http://localhost:9500/eureka-client/foo", String.class);
    }

    @GetMapping("/foo")
    public String foo() {
        return restTemplate.getForObject("http://localhost:9000/foo", String.class);
    }

    @GetMapping("/hi")
    public String hi(@RequestParam String name){
        return name;
    }
}
