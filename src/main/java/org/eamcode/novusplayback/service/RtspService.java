package org.eamcode.novusplayback.service;

import org.eamcode.novusplayback.dto.PlaybackRequest;
import org.eamcode.novusplayback.dto.StreamType;
import org.eamcode.novusplayback.util.NovusTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


@Service
public class RtspService {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public RtspService(
            @Value("${novus.host}") String host,
            @Value("${novus.port}") int port,
            @Value("${novus.username}") String username,
            @Value("${novus.password}") String password
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String buildRtspUrl(PlaybackRequest req) {

        int timeLen = resolveTimeLen(req);
        String streamType = resolveStreamType(req);
        String novusTime = NovusTimeFormatter.formatNovusTime(req.time());

        return createString(
                req.camera(),
                req.date().toString(),
                novusTime,
                timeLen,
                streamType
        );
    }

    private String createString(
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

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public int resolveTimeLen(PlaybackRequest req) {
        Integer tl = req.timeLen();
        return (tl == null ? 60 : tl);
    }

    private String resolveStreamType(PlaybackRequest req) {
        String raw = (req.streamType() == null ? StreamType.MAIN.toString() : req.streamType());
        return raw.toLowerCase(Locale.ROOT);
    }
}
