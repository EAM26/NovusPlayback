package org.eamcode.novusplayback.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NovusTimeFormatter {


    private static final DateTimeFormatter NOVUS_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");


    public static String formatNovusTime(LocalTime time) {
        return time.format(NOVUS_TIME);
    }

    public static String formatFileNameTime(LocalTime time) {
        return time.format(NOVUS_TIME).replace(":", "-");
    }

}
