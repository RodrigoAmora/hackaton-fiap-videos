package br.com.fiap.fiapvideos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class VideoException extends RuntimeException {

    public VideoException(String message) {
        super(message);
    }

}
