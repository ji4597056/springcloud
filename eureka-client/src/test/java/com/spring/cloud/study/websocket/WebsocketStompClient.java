package com.spring.cloud.study.websocket;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * @author Jeffrey
 * @since 2017/11/12 9:55
 */
public class WebsocketStompClient {

    static public class MyStompSessionHandler
        extends StompSessionHandlerAdapter {

        private String userId;

        private String wsSendUri;

        private String wsSubscribeUri;

        public MyStompSessionHandler(String userId, String wsSendUri, String wsSubscribeUri) {
            this.userId = userId;
            this.wsSendUri = wsSendUri;
            this.wsSubscribeUri = wsSubscribeUri;
        }

        private void showHeaders(StompHeaders headers) {
            for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                System.err.print("  " + e.getKey() + ": ");
                boolean first = true;
                for (String v : e.getValue()) {
                    if (!first) {
                        System.err.print(", ");
                    }
                    System.err.print(v);
                    first = false;
                }
                System.err.println();
            }
        }

        private void sendJsonMessage(StompSession session) {
            String msg = String.format("hello from %s", userId);
            session.send(wsSendUri, msg);
        }

        private void subscribeTopic(String topic, StompSession session) {
            session.subscribe(topic, new StompFrameHandler() {

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers,
                    Object payload) {
                    System.err.println(
                        String.format("%s receive message: %s", userId, payload.toString()));
                }
            });
        }

        @Override
        public void afterConnected(StompSession session,
            StompHeaders connectedHeaders) {
            System.err.println("Connected! Headers:");
            showHeaders(connectedHeaders);

            subscribeTopic(wsSubscribeUri, session);
            sendJsonMessage(session);
        }
    }

    public static void startClient(String wsConnectUrl, String wsSendUri, String wsSubscribeUri,
        long sleepForSecond)
        throws Exception {
        WebSocketClient simpleWebSocketClient =
            new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient =
            new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());

        String userId = "Mr.J---" + new Random().nextInt(1000);
        StompSessionHandler sessionHandler = new MyStompSessionHandler(userId, wsSendUri,
            wsSubscribeUri);
        StompSession session = stompClient.connect(wsConnectUrl, sessionHandler)
            .get();
        Long i = 0L;
        while (true) {
            TimeUnit.SECONDS.sleep(sleepForSecond);
            String msg = String.format("%s send %s !", userId, i++);
            session.send(wsSendUri, msg);
        }
    }

    public static void startClient(String wsConnectUrl, String wsSendUri, String wsSubscribeUri)
        throws Exception {
        startClient(wsConnectUrl, wsSendUri, wsSubscribeUri, 1L);
    }
}
