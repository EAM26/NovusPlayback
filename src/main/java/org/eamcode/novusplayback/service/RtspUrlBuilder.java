package org.eamcode.novusplayback.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class RtspUrlBuilder {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public RtspUrlBuilder(
            @Value("${novus.host}") String host,
            @Value("${novus.port}") int port,
            @Value("${NOVUS_USERNAME}") String username,
            @Value("${NOVUS_PASSWORD}") String password
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String build(
            int camera,
            String date,
            String time,
            int timeLen,
            String streamType
    ) {
        return "rtsp://" +
                encode(username) + ":" + encode(password) + "@" +
                host + ":" + port +
                "/chTD=" + camera +
                "&date=" + date +
                "&time=" + time +
                "&timelen=" + timeLen +
                "&streamType=" + streamType;
    }

    public String getHost() {
        return host;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
