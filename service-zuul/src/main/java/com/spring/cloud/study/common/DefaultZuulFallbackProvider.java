package com.spring.cloud.study.common;

import com.netflix.zuul.context.RequestContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

/**
 * default zuul fallback
 *
 * @author Jeffrey
 * @since 2017/06/29 14:48
 */
public class DefaultZuulFallbackProvider implements ZuulFallbackProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultZuulFallbackProvider.class);

    private static final HttpStatus DEFAULT_FALLBACK_STATUS = HttpStatus.SERVICE_UNAVAILABLE;

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }

            @Override
            public InputStream getBody() throws IOException {
                RequestContext ctx = RequestContext.getCurrentContext();
                String message = ctx.get("serviceId").toString() + "服务繁忙,请稍后再试";
                return new ByteArrayInputStream(message.getBytes());
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {
                return DEFAULT_FALLBACK_STATUS;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return DEFAULT_FALLBACK_STATUS.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return DEFAULT_FALLBACK_STATUS.getReasonPhrase();
            }

            @Override
            public void close() {
                RequestContext ctx = RequestContext.getCurrentContext();
                try {
                    ctx.getResponseDataStream().close();
                } catch (IOException e) {
                    logger.error("Fall back close response error, exception : {}", e);
                }
            }
        };
    }

}
