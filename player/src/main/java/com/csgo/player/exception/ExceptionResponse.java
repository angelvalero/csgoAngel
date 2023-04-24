package com.csgo.player.exception;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ExceptionResponse {

    private LocalDate localdate;
    private String message;
    private List<String> details;

    public ExceptionResponse(LocalDate localdate, String message, List<String> details) {
        super();
        this.localdate = localdate;
        this.message = message;
        this.details = details;

    }

}
