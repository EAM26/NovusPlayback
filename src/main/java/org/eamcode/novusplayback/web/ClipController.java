package org.eamcode.novusplayback.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.eamcode.novusplayback.dto.PlaybackRequest;
import org.eamcode.novusplayback.service.RtspService;
import org.eamcode.novusplayback.util.NovusTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ClipController {

    private final RtspService rtspService;
    private static final Logger log = LoggerFactory.getLogger(ClipController.class);

    public ClipController(RtspService rtspService) {
        this.rtspService = rtspService;
    }

    @GetMapping(value = "/api/clip.mp4", produces = "video/mp4")
    public void clipMp4(@Valid @ModelAttribute PlaybackRequest request, HttpServletResponse response, BindingResult bindingResult) throws Exception {
        System.out.println("Received clip request: " + request);
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters");
            return;
        }
        String rtspUrl = rtspService.buildRtspUrl(request);

        boolean download = Boolean.TRUE.equals(request.download());

        log.info(
                "Clip start: cam={}, date={}, time={}, timeLen={}, streamType={}, download={}",
                request.camera(),
                request.date(),
                request.time(),
                request.timeLen(),
                request.streamType(),
                download
        );

        String fileName = makeFileName(request);

        response.setContentType("video/mp4");
        String disposition = download ? "attachment" : "inline";
        response.setHeader("Content-Disposition",   disposition+ "; filename=\"" + fileName + ".mp4\"");
        response.setHeader("Cache-Control", "no-store");

        Process p = getProcess(request, rtspUrl);

        try (InputStream in = p.getInputStream()) {
            in.transferTo(response.getOutputStream());
            response.flushBuffer();
        } finally {
            p.destroy();
        }
    }

    private String makeFileName(PlaybackRequest request) {
        String timeAsString = NovusTimeFormatter.formatFileNameTime(request.time());
        return "clip-%s_%s-cam%d".formatted(
                request.date(),
                timeAsString,
                request.camera()
        );
    }

    private static Process getProcess(PlaybackRequest request, String rtspUrl) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add("ffmpeg");
        cmd.add("-hide_banner");
        cmd.add("-loglevel");
        cmd.add("error");
        cmd.add("-rtsp_transport");
        cmd.add("tcp");
        cmd.add("-i");
        cmd.add(rtspUrl);

        cmd.add("-t");
        cmd.add(String.valueOf(request.timeLen()));

        cmd.add("-an");
        cmd.add("-c");
        cmd.add("copy");

        cmd.add("-f");
        cmd.add("mp4");
        cmd.add("-movflags");
        cmd.add("frag_keyframe+empty_moov");

        cmd.add("pipe:1");

        Process p = new ProcessBuilder(cmd).start();

        drainAsync(p.getErrorStream());

        return p;
    }

    private static void drainAsync(InputStream err) {
        Thread t = new Thread(() -> {
            try {
                err.transferTo(OutputStream.nullOutputStream());
            } catch (IOException ignored) {
            }
        }, "ffmpeg-stderr-drain");
        t.setDaemon(true);
        t.start();
    }


//    private static Process getProcess(PlaybackRequest request, String rtspUrl) throws IOException {
//        List<String> cmd = new ArrayList<>();
//        cmd.add("ffmpeg");
//        cmd.add("-hide_banner");
//        cmd.add("-loglevel");
//        cmd.add("error");
//        cmd.add("-rtsp_transport");
//        cmd.add("tcp");
//        cmd.add("-i");
//        cmd.add(rtspUrl);
//
//        cmd.add("-t");
//        cmd.add(String.valueOf(request.timeLen()));
//
//        cmd.add("-an");
//
//        cmd.add("-c");
//        cmd.add("copy");
//
//        cmd.add("-f");
//        cmd.add("mp4");
//        cmd.add("-movflags");
//        cmd.add("frag_keyframe+empty_moov");
//
//        cmd.add("pipe:1");
//
//        ProcessBuilder pb = new ProcessBuilder(cmd);
//        pb.redirectErrorStream(true);
//        return pb.start();
//    }

}
