package com.spring.cloud.study.websocket;

import java.util.Random;
import java.util.stream.IntStream;
import org.junit.Test;

/**
 * @author Jeffrey
 * @since 2017/11/12 10:05
 */
public class WebsocketStompClientTest {

    private static final String WS_CONNECT_URL = "ws://localhost:8080/eureka-client/websocket";

    private static final String WS_SEND_URI_PREFIX = "/test";

    private static final String WS_SUBSCRIBE_URI_PREFIX = "/topic";

    private static final String WS_SEND_URI = WS_SEND_URI_PREFIX + "/message1/";

    private static final String WS_SUBSCRIBE_URI = WS_SUBSCRIBE_URI_PREFIX + "/message1/";

    private static final String WS_URI_QUERY_STR = "?sid=123&password=3112";

    @Test
    public void testClient() throws Exception {
        int random = new Random().nextInt(1000);
        WebsocketStompClient.startClient(WS_CONNECT_URL + WS_URI_QUERY_STR, WS_SEND_URI_PREFIX + "/message2/" + random,
            WS_SUBSCRIBE_URI_PREFIX + "/message2/" + random);
    }

    @Test
    public void testMuchClient() throws Exception {
        final int clientNum = 3;
        IntStream.range(0, clientNum).forEach(
            i -> new Thread(() -> {
                try {
                    int random = new Random().nextInt(10000);
                    WebsocketStompClient
                        .startClient(WS_CONNECT_URL + WS_URI_QUERY_STR, WS_SEND_URI + random,
                            WS_SUBSCRIBE_URI + random, i + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start());
        System.in.read();
    }
}
