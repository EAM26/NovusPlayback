package org.eamcode.novusplayback.web;

import jakarta.validation.Valid;
import org.eamcode.novusplayback.dto.PlaybackRequest;
import org.eamcode.novusplayback.dto.RtspResponse;
import org.eamcode.novusplayback.dto.StreamType;
import org.eamcode.novusplayback.service.RtspService;
import org.eamcode.novusplayback.service.RtspUrlBuilder;
import org.eamcode.novusplayback.util.NovusTimeFormatter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api")
public class RtspController {

    private final RtspService rtspService;

    public RtspController(RtspService rtspService) {
        this.rtspService = rtspService;
    }

    @PostMapping("/rtsp-url")
    public RtspResponse build(@Valid @RequestBody PlaybackRequest req) {
        return new RtspResponse(rtspService.buildRtspUrl(req));
    }

}
