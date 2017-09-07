package com.spring.cloud.study.service;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Jeffrey
 * @since 2017/05/10 14:49
 */
@Component
public class HiServiceHystric implements HiService{

    @Override
    public String sayHiFromClientOne(@RequestParam String name) {
        return "sorry" + name;
    }
}
