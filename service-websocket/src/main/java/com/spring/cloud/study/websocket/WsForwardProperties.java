package com.spring.cloud.study.websocket;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * websocket forward properties
 *
 * @author Jeffrey
 * @since 2017/11/29 11:17
 */
@ConfigurationProperties("ws.forward")
public class WsForwardProperties {

    /**
     * enabled
     */
    private boolean enabled = true;

    /**
     * key:一般用于路由前缀(DiscoveryForwardHandler),value:ForwardHandler
     */
    private Map<String, ForwardHandler> handlers = new HashMap<>(5);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, ForwardHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(
        Map<String, ForwardHandler> handlers) {
        this.handlers = handlers;
    }

    @PostConstruct
    public void init() {
        handlers.forEach((key, handler) -> {
            if (!StringUtils.hasText(handler.getId())) {
                handler.id = key;
            }
        });
    }

    public static class ForwardHandler {

        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * id
         */
        private String id;

        /**
         * 转发前缀(用于EurekaForwardHandler)
         * 需要加"/",eg:/test
         */
        private String forwardPrefix;

        /**
         * websocket handler uri
         */
        private String uri;

        /**
         * service id
         * 主要用于从eureka中根据serviceId获取service address
         */
        private String serviceId;

        /**
         * withJs
         */
        private boolean withJs;

        /**
         * 是否直接转发
         * false则加id作为路由前缀,当使用DiscoveryForwardHandler使用默认值
         */
        private boolean direct;

        /**
         * list of servers
         * 若无service id,则从中获取地址列表
         */
        private String[] listOfServers;

        /**
         * handler class name
         */
        private String handler;

        /**
         * allowedOrigins
         */
        private String[] allowedOrigins;

        /**
         * interceptor class name array.
         * 若为nul,则添加全局拦截器,若不为null,则以改拦截器为主
         */
        private String[] interceptors;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public boolean isWithJs() {
            return withJs;
        }

        public void setWithJs(boolean withJs) {
            this.withJs = withJs;
        }

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public boolean isDirect() {
            return direct;
        }

        public void setDirect(boolean direct) {
            this.direct = direct;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public String[] getInterceptors() {
            return interceptors;
        }

        public void setInterceptors(String[] interceptors) {
            this.interceptors = interceptors;
        }

        public String getForwardPrefix() {
            return forwardPrefix;
        }

        public void setForwardPrefix(String forwardPrefix) {
            this.forwardPrefix = forwardPrefix;
        }

        public String[] getListOfServers() {
            return listOfServers;
        }

        public void setListOfServers(String[] listOfServers) {
            this.listOfServers = listOfServers;
        }
    }
}
