package org.eamcode.novusplayback.web;

import jakarta.validation.Valid;
import org.eamcode.novusplayback.dto.PlaybackRequest;
import org.eamcode.novusplayback.dto.RtspResponse;
import org.eamcode.novusplayback.service.RtspUrlBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RtspUrlController {

    private final RtspUrlBuilder builder;

    public RtspUrlController(RtspUrlBuilder builder) {
        this.builder = builder;
    }

    @PostMapping("/rtsp-url")
    public RtspResponse build(@Valid @RequestBody PlaybackRequest req) {
        String url = builder.build(
                req.camera(),
                req.date(),
                req.time(),
                req.timeLen(),
                req.streamType()

                );
        return new RtspResponse(url);
    }
}
