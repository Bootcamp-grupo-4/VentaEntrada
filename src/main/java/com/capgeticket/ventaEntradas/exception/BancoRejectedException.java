package com.capgeticket.ventaEntradas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BancoRejectedException extends RuntimeException {
    public BancoRejectedException(String message) {
        super(message);
    }
}
