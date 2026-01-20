package org.eamcode.novusplayback.web;

import lombok.extern.slf4j.Slf4j;
import org.eamcode.novusplayback.dto.StreamType;
import org.eamcode.novusplayback.model.WatchForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;


@Controller
@Slf4j
public class WatchPageController {


    @GetMapping("/watch")
    public String watchPage(
            @RequestParam(name = "camera", required = false) Integer camera,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "time", required = false) LocalTime time,
            @RequestParam(name = "timeLen", required = false) Integer timeLen,
            @RequestParam(name = "streamType", required = false) String streamType,
            Model model
    ) {
        log.info("Request to watch made by {}", "John");
        WatchForm form = new WatchForm();
        form.setCamera(camera == null ? 1 : camera);
        form.setDate(date == null ? LocalDate.now() : date);
        form.setTime(time == null ? LocalTime.now().withNano(0) : time.withNano(0));
        form.setTimeLen(timeLen == null ? 60 : timeLen);

        String st = (streamType == null ? StreamType.MAIN.toString() : streamType)
                .toLowerCase(Locale.ROOT);
        form.setStreamType(st);

        model.addAttribute("form", form);
        return "watch";
    }

}
