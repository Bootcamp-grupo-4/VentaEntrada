package com.capgeticket.ventaEntradas.exception;

import com.capgeticket.ventaEntradas.response.BancoResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BancoRejectedException extends RuntimeException {

    @Getter
    private String errorCode;

    public BancoRejectedException(String message) {
        super(message);
    }

    public BancoRejectedException(String code, String message) {
        super(message);
        this.errorCode = code;
    }
}
