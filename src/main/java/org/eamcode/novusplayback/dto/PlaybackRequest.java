package org.eamcode.novusplayback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlaybackRequest(
        @Min(1) int camera,
        @NotNull LocalDate date,
        @NotNull LocalTime startTime,
        @Min(1) @Max(300) Integer timeLen,
        String streamType
) {
}
