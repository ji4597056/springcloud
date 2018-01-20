package com.spring.cloud.study.config;

import com.spring.cloud.study.websocket.AbstractForwardHandler;
import com.spring.cloud.study.websocket.DiscoveryForwardHandler;
import com.spring.cloud.study.websocket.TestForwardInterceptor;
import com.spring.cloud.study.websocket.WsForwardProperties;
import com.spring.cloud.study.websocket.WsForwardProperties.ForwardHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * websocket forward config
 * TODO
 * 1.增加动态注入interceptor,handler
 * 2.提取forwardresolve(is forward,message convert)
 * 3.SimpleForwardHandler
 * 4.提取接口(client,client handler)
 *
 * @author Jeffrey
 * @since 2017/1/22 18:14
 */
@Configuration
@EnableWebSocket
@ConditionalOnClass({WebSocketHandler.class})
@ConditionalOnProperty(
    prefix = "ws.forward",
    name = {"enabled"},
    havingValue = "true"
)
@EnableConfigurationProperties({WsForwardProperties.class})
public class WebSocketForwardConfig implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketForwardConfig.class);

    @Autowired
    WsForwardProperties wsForwardProperties;

    @Autowired
    AbstractForwardHandler forwardHandler;

    @Autowired(required = false)
    List<HandshakeInterceptor> handshakeInterceptors;

    /**
     * 存放HandshakeInterceptor实例的对象池(HandshakeInterceptor必须有无参构造函数)
     */
    private Map<String, HandshakeInterceptor> interceptorInstances;

    /**
     * 存放AbstractForwardHandler实例的对象池(AbstractForwardHandler必须有无参构造函数)
     */
    private Map<String, AbstractForwardHandler> handlerInstances;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        Map<String, ForwardHandler> handlers = wsForwardProperties.getHandlers();
        if (handlers != null && !handlers.isEmpty()) {
            wsForwardProperties.getHandlers()
                .forEach((id, handler) -> {
                    if (handler.isEnabled()) {
                        registryHandler(webSocketHandlerRegistry, handler);
                    }
                });
        }
    }

    @Bean
    @ConditionalOnMissingBean({AbstractForwardHandler.class})
    public AbstractForwardHandler messageWebSocketHandler(DiscoveryClient discoveryClient) {
        return new DiscoveryForwardHandler(discoveryClient, wsForwardProperties);
    }

    @Bean
    public HandshakeInterceptor messageWebSocketInterceptor() {
        return new TestForwardInterceptor();
    }

    /**
     * 注册handler
     *
     * @param registry WebSocketHandlerRegistry
     * @param handler ForwardHandler
     */
    private void registryHandler(WebSocketHandlerRegistry registry, ForwardHandler handler) {
        WebSocketHandlerRegistration registration = getRegistration(registry, handler);
        // set allowedOrigins
        if (handler.getAllowedOrigins() == null) {
            registration.setAllowedOrigins("*");
        } else {
            registration.setAllowedOrigins(handler.getAllowedOrigins());
        }
        // set interceptors
        String[] classNames = handler.getInterceptors();
        if (classNames != null) {
            HandshakeInterceptor[] interceptors = Arrays.stream(classNames)
                .map(className -> {
                    try {
                        if (interceptorInstances == null) {
                            interceptorInstances = new HashMap<>(3);
                        }
                        // 从对象池中获取拦截器实例
                        return interceptorInstances.putIfAbsent(className,
                            (HandshakeInterceptor) Class.forName(className).newInstance());
                    } catch (Exception e) {
                        LOGGER.error("Set webosocket interceptors error!error: {}", e);
                        throw new IllegalArgumentException("Set websocket interceptors error!");
                    }
                }).toArray(HandshakeInterceptor[]::new);
            registration.addInterceptors(interceptors);
        } else {
            if (handshakeInterceptors != null) {
                registration.addInterceptors(handshakeInterceptors
                    .toArray(new HandshakeInterceptor[handshakeInterceptors.size()]));
            }
        }
        // set withSocketJs
        if (handler.isWithJs()) {
            registration.withSockJS();
        }
    }

    /**
     * 获取WebSocketHandlerRegistration
     *
     * @param registry WebSocketHandlerRegistry
     * @param handler ForwardHandler
     * @return WebSocketHandlerRegistration
     */
    private WebSocketHandlerRegistration getRegistration(WebSocketHandlerRegistry registry,
        ForwardHandler handler) {
        // set handler uri
        String handlerUri = handler.getUri();
        if (!handler.isDirect()) {
            handlerUri = "/" + handler.getId() + handler.getUri();
        }
        // set handler
        String className = handler.getHandler();
        if (className == null) {
            return registry.addHandler(forwardHandler, handlerUri);
        } else {
            try {
                if (handlerInstances == null) {
                    handlerInstances = new HashMap<>(3);
                }
                // 从对象池中获取处理器实例
                AbstractForwardHandler realHander = handlerInstances.putIfAbsent(className,
                    (AbstractForwardHandler) Class.forName(className).newInstance());
                return registry.addHandler(realHander, handlerUri);
            } catch (Exception e) {
                LOGGER.error("Set webosocket handler error!error: {}", e);
                throw new IllegalArgumentException("Set websocket handler error!");
            }
        }
    }
}
