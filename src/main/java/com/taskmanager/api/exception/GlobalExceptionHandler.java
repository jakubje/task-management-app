package com.taskmanager.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleTaskNotFound(TaskNotFoundException ex) {
        return Mono.just(ex.getMessage());
    }

    @ExceptionHandler(InvalidSubtaskIndexException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleInvalidSubtaskIndex(InvalidSubtaskIndexException ex){
        return Mono.just(ex.getMessage());
    }

}
