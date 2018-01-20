package com.spring.cloud.study.util;

import com.google.common.base.Charsets;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.springframework.web.util.UriUtils;

/**
 * @author Jeffrey
 * @since 2017/11/29 10:23
 */
public class UrlUtils {

    private UrlUtils() {
        throw new AssertionError("No UrlUtils instances for you!");
    }

    /**
     * 根据url参数字符串获取参数map
     *
     * @param urlQueryStr url param query string
     * @return param map
     */
    public static Map<String, List<String>> getParamMap(String urlQueryStr) {
        Map<String, List<String>> params = new HashMap<>(6);
        StringTokenizer st = new StringTokenizer(urlQueryStr, "&");
        int i;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            i = s.indexOf("=");
            try {
                if (i > 0 && s.length() >= i + 1) {
                    String name = s.substring(0, i);
                    String value = s.substring(i + 1);
                    name = UriUtils.decode(name, Charsets.UTF_8.name());
                    value = UriUtils.decode(value, Charsets.UTF_8.name());
                    List<String> valueList = params.computeIfAbsent(name, k -> new LinkedList<>());
                    valueList.add(value);
                } else if (i == -1) {
                    String name = s;
                    String value = "";
                    name = UriUtils.decode(name, Charsets.UTF_8.name());
                    List<String> valueList = params.computeIfAbsent(name, k -> new LinkedList<>());
                    valueList.add(value);
                }
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }
        return params;
    }

    /**
     * 根据uri参数
     *
     * @param uri uri
     * @return path list
     */
    public static List<String> getPathList(String uri) {
        if (uri.charAt(0) == '/') {
            return Arrays.stream(uri.substring(1).split("/")).collect(Collectors.toList());
        } else {
            return Arrays.stream(uri.split("/")).collect(Collectors.toList());
        }
    }

    /**
     * 获取url
     *
     * @param scheme scheme
     * @param address address
     * @param path path
     * @param query query
     * @return url
     */
    public static String getUrl(String scheme, String address, String path, String query) {
        if (query == null) {
            return scheme + "://" + address + path;
        }
        return scheme + "://" + address + path + "?" + query;
    }
}
