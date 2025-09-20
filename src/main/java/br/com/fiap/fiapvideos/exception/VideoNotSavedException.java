package br.com.fiap.fiapvideos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class VideoNotSavedException extends RuntimeException {

    public VideoNotSavedException(String message) {
        super(message);
    }

}
