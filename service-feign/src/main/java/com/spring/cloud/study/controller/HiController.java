package com.spring.cloud.study.controller;

import com.spring.cloud.study.service.HiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jeffrey
 * @since 2017/05/10 14:29
 */
@RestController
public class HiController {

    @Autowired
    private HiService hiService;

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        return hiService.sayHiFromClientOne(name);
    }
}
