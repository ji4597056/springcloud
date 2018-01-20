package com.spring.cloud.study.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * websocket handler
 *
 * @author Jeffrey
 * @since 2017/01/22 17:33
 */
public abstract class AbstractForwardHandler extends AbstractWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractForwardHandler.class);

    /**
     * channel map(key:session id, value:channel)
     */
    private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>(100);

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
        throws Exception {
        CHANNELS.get(session.getId())
            .writeAndFlush(new TextWebSocketFrame(convertMessage(message)));
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebsocketForwardClient client = WebsocketForwardClient
            .create(getForwardUrl(session), session);
        client.connect();
        Channel channel = client.getChannel();
        CHANNELS.put(session.getId(), channel);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
        throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        closeGracefully(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        throws Exception {
        closeGracefully(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取转发url
     *
     * @param session websocket session
     * @return forward url
     */
    public abstract String getForwardUrl(WebSocketSession session);

    /**
     * 转换消息
     *
     * @param message text message
     * @return convert message
     */
    public String convertMessage(TextMessage message) {
        return message.getPayload();
    }

    /**
     * close client
     */
    private void closeGracefully(WebSocketSession session) {
        Optional.ofNullable(CHANNELS.get(session.getId()))
            .ifPresent(channel -> {
                try {
                    if (channel.isOpen()) {
                        channel.closeFuture();
                    }
                } catch (Exception e) {
                    LOGGER.warn("Close websocket forward client error!error: {}", e);
                }
            });
        CHANNELS.remove(session.getId());
    }
}
