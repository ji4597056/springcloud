package com.spring.cloud.study.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * @author Jeffrey
 * @since 2017/11/10 17:43
 */
@Controller
public class WebsocketMessageController {

    @MessageMapping("/message1/{random}")
//    @SendToUser(value = "/topic/message", broadcast = false)
    public Object sendMessage1(String message) throws Exception {
        System.out.println("receive message1:" + message);
        return "message1," + message;
    }

    @MessageMapping("/message2/{random}")
//    @SendToUser(value = "/topic/message", broadcast = false)
    public Object sendMessage2(String message) throws Exception {
        System.out.println("receive message2:" + message);
        return "message2," + message;
    }
}
