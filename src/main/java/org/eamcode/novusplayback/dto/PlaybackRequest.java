package org.eamcode.novusplayback.dto;

public record PlaybackRequest(
        int camera,
        String date,
        String time,
        int timeLen,
        String streamType
) {
}
