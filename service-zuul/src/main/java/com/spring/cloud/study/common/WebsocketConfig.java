package com.spring.cloud.study.common;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

/**
 * @author Jeffrey
 * @since 2017/11/12 15:08
 */
@Configuration
public class WebsocketConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new MessageConverter(){

            private MessageConverter messageConverter = new StringMessageConverter();

            @Override
            public Object fromMessage(Message<?> message, Class<?> aClass) {
                if (aClass == Object.class){
                    aClass = String.class;
                }
                return messageConverter.fromMessage(message, aClass);
            }

            @Override
            public Message<?> toMessage(Object o, MessageHeaders messageHeaders) {
                return messageConverter.toMessage(o, messageHeaders);
            }
        };
    }
}
