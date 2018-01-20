package com.spring.cloud.study.websocket;

import com.spring.cloud.study.websocket.WsForwardProperties.ForwardHandler;
import io.netty.util.CharsetUtil;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriUtils;

/**
 * eureka forward handler
 *
 * @author Jeffrey
 * @since 2017/11/29 13:09
 */
public class DiscoveryForwardHandler extends AbstractForwardHandler {

    /**
     * 初始化标志
     */
    private volatile boolean dirty = true;

    /**
     * 负载均衡计数器
     * TODO 每个服务对应一个
     */
    private AtomicInteger count = new AtomicInteger();

    /**
     * key:patternComparator,value:url path
     */
    private Map<String, ForwardHandler> serviceMap = new ConcurrentHashMap<>();

    private PathMatcher matcher = new AntPathMatcher();

    private DiscoveryClient discoveryClient;

    private WsForwardProperties wsForwardProperties;

    public DiscoveryForwardHandler(DiscoveryClient discoveryClient,
        WsForwardProperties wsForwardProperties) {
        this.discoveryClient = discoveryClient;
        this.wsForwardProperties = wsForwardProperties;
    }

    @Override
    public String getForwardUrl(WebSocketSession session) {
        init();
        ForwardHandler handler = getServiceId(session.getUri().getPath());
        String address = getLoadBalanceInstance(handler);
        return getWsForwardUrl(address, handler, session.getUri());
    }

    /**
     * 获取转发url
     *
     * @param address address
     * @param handler ForwardHandler
     * @param uri uri
     * @return forward url
     */
    private String getWsForwardUrl(String address, ForwardHandler handler, URI uri) {
        String prefix = Optional.ofNullable(handler.getForwardPrefix()).orElse("");
        try {
            String query = UriUtils.encodeQuery(uri.getQuery(), CharsetUtil.UTF_8.name());
            String url = UriUtils.encodePath(uri.getPath().substring(uri.getPath().indexOf('/', 1)),
                CharsetUtil.UTF_8.name());
            if (query == null) {
                return "ws://" + prefix + address + url;
            }
            return "ws://" + prefix + address + url + "?" + query;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取ForwardHandler
     *
     * @param uri uri
     * @return service id
     */
    private ForwardHandler getServiceId(String uri) {
        return serviceMap.entrySet().stream().filter(entry -> matcher.match(entry.getKey(), uri))
            .findFirst().map(Entry::getValue)
            .orElseThrow(
                () -> new IllegalStateException("Can't find matching patterns for " + uri));
    }

    /**
     * 获取负载均衡ServiceInstance
     *
     * @param handler forward handler
     * @return address
     */
    private String getLoadBalanceInstance(ForwardHandler handler) {
        List<String> addresses;
        if (discoveryClient != null && handler.getServiceId() != null) {
            // 若有service id,从discoveryClient获取地址
            List<ServiceInstance> instances = discoveryClient.getInstances(handler.getServiceId());
            if (instances == null || instances.isEmpty()) {
                throw new IllegalStateException("Can't find service id for " + handler.getId());
            }
            addresses = instances.stream()
                .map(instance -> instance.getHost() + ":" + instance.getPort()).collect(
                    Collectors.toList());
        } else if (handler.getListOfServers() != null && handler.getListOfServers().length > 0) {
            // 若无service id,从listOfServers获取地址
            addresses = Arrays.asList(handler.getListOfServers());
        } else {
            throw new IllegalStateException(
                "Can't find service id or listOfServers for " + handler.getId());
        }
        int index;
        while (true) {
            int current = count.get();
            int next = (current + 1) % addresses.size();
            if (count.compareAndSet(current, next)) {
                index = next;
                break;
            }
        }
        return addresses.get(index);
    }

    /**
     * 初始化serviceMap
     */
    private void init() {
        if (dirty) {
            synchronized (this) {
                if (this.dirty) {
                    wsForwardProperties.getHandlers().forEach(
                        (id, handler) -> serviceMap
                            .put("/" + handler.getId() + handler.getUri(), handler));
                    this.dirty = false;
                }
            }
        }
    }
}
