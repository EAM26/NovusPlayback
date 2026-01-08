package org.eamcode.novusplayback.web;

import jakarta.validation.Valid;
import org.eamcode.novusplayback.dto.PlaybackRequest;
import org.eamcode.novusplayback.dto.RtspResponse;
import org.eamcode.novusplayback.dto.StreamType;
import org.eamcode.novusplayback.service.RtspUrlBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api")
public class RtspUrlController {

    private final RtspUrlBuilder builder;

    public RtspUrlController(RtspUrlBuilder builder) {
        this.builder = builder;
    }

    @PostMapping("/rtsp-url")
    public RtspResponse build(@Valid @RequestBody PlaybackRequest req) {
        String streamType = (req.streamType() == null ? StreamType.MAIN.toString() : req.streamType())
                .toLowerCase(Locale.ROOT);
        int timeLen = (req.timeLen() == null ? 60 : req.timeLen());
        String url = builder.build(
                req.camera(),
                req.date().toString(),
                req.startTime().withNano(0).toString(),
                timeLen,
                streamType

                );
        return new RtspResponse(url);
    }
}
