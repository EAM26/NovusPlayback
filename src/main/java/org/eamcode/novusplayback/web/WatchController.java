package org.eamcode.novusplayback.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatchController {

    @GetMapping(value = "/watch", produces = MediaType.TEXT_HTML_VALUE)
    public String watchPage() {
        return """
                <!doctype html>
                <html lang="nl">
                <head>
                  <meta charset="utf-8">
                  <title>Playback MVP</title>
                </head>
                <body style="font-family: sans-serif; padding: 16px;">
                  <h3>Playback MVP (browser)</h3>
                
                  <form id="f" style="display: grid; gap: 8px; max-width: 420px;">
                    <label>Camera <input name="camera" type="number" value="1" required></label>
                    <label>Date <input name="date" type="date" required></label>
                    <label>Time <input name="time" type="time" step="1" required></label>
                    <label>TimeLen (sec) <input name="timeLen" type="number" value="60" min="1" max="300" required></label>
                    <label>StreamType <input name="streamType" type="text" value="main" required></label>
                    <button type="submit">Bekijk</button>
                  </form>
                
                  <div style="margin-top: 16px;">
                    <video id="v" controls style="width: 100%; max-width: 900px;"></video>
                  </div>
                
                  <script>
                    const form = document.getElementById("f");
                    const video = document.getElementById("v");
                
                    form.addEventListener("submit", (e) => {
                      e.preventDefault();
                
                      const params = new URLSearchParams(new FormData(form));
                      video.src = "/api/clip.mp4?" + params.toString();
                      video.load();
                      video.play().catch(() => {});
                    });
                  </script>
                </body>
                </html>
                """;
    }
}
