package org.eamcode.novusplayback.web;

import jakarta.validation.Valid;
import org.eamcode.novusplayback.dto.PlaybackRequest;
import org.eamcode.novusplayback.dto.RtspResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class VlcOpenController {

    private final RtspUrlController rtspUrlController;

    public VlcOpenController(RtspUrlController rtspUrlController) {
        this.rtspUrlController = rtspUrlController;
    }

    @GetMapping(value = "/open-vlc.m3u", produces = "audio/x-mpegurl")
    public ResponseEntity<byte[]> openVlcM3u(@Valid @ModelAttribute PlaybackRequest request) {

        RtspResponse response = rtspUrlController.build(request);

        // Dutch comments: jouw bestaande RtspResponse accessor
        String rtspUrl = response.rtspUrl();

        // Dutch comments: m3u is simpel: header + 1 URL
        String m3u = "#EXTM3U\n" + rtspUrl + "\n";
        byte[] bytes = m3u.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                // Dutch comments: force download zodat de browser het als bestand behandelt
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"playback.m3u\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("audio/x-mpegurl"))
                .body(bytes);
    }



//    @GetMapping(value = "/open-vlc", produces = MediaType.TEXT_HTML_VALUE)
//    public String openVlc(@ModelAttribute PlaybackRequest request) {
//        RtspResponse response = rtspUrlController.build(request);
//
//        String rtspUrl = response.rtspUrl();
//        String vlcUrl = "vlc://" + rtspUrl.replaceFirst("^rtsp://", "");
//
//        String safeVlcUrl = htmlEscape(vlcUrl);
//        String safeRtspUrl = htmlEscape(rtspUrl);
//
//        return """
//                <!doctype html>
//                <html lang="en">
//                <head><meta charset="utf-8"><title>Open VLC</title></head>
//                <body style="font-family: sans-serif; padding: 16px;">
//                  <h3>Open playback in VLC</h3>
//                  <p>
//                    <a href="%s">Open in VLC (vlc://)</a>
//                  </p>
//                  <p>
//                    Fallback:
//                    <a href="%s">Open as RTSP (rtsp://)</a>
//                  </p>
//                </body>
//                </html>
//                """.formatted(safeVlcUrl, safeRtspUrl);
//
//
//    }
//
//    private static String htmlEscape(String s) {
//        return s.replace("&", "&amp;")
//                .replace("\"", "&quot;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;");
//    }
}

