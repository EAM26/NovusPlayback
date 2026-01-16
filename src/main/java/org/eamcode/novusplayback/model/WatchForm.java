package org.eamcode.novusplayback.model;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WatchForm {
    private Integer camera;
    private LocalDate date;
    private LocalTime time;
    private Integer timeLen;
    private String streamType;

}
