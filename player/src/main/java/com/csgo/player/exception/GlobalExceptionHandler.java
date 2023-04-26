package com.csgo.player.exception;


import java.time.LocalDate;

import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


import lombok.extern.slf4j.Slf4j;


    @Slf4j
    @ControllerAdvice
    public class GlobalExceptionHandler {


        @ExceptionHandler
        public ResponseEntity<ExceptionResponse> handlerException(EntityNotFoundException exception,WebRequest req){
            ExceptionResponse res = new ExceptionResponse(LocalDate.now(), exception.getMessage(),null);

            return new ResponseEntity<ExceptionResponse>(res, HttpStatus.NOT_FOUND);
        };

    }
