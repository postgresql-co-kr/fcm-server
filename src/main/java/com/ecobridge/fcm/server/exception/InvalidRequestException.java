package com.ecobridge.fcm.server.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends IllegalArgumentException {
    private String errorCode;
    private String message;
    public InvalidRequestException(String message) {
        super(String.format("[%s] : [%s]", "BAD_REQUEST", message));
        this.errorCode = "BAD_REQUEST";
        this.message = message;
    }
}
