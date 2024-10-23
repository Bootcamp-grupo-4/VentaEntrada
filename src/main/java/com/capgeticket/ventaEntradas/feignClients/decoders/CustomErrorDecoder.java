package com.capgeticket.ventaEntradas.feignClients.decoders;

import com.capgeticket.ventaEntradas.exception.BancoRejectedException;
import com.capgeticket.ventaEntradas.exception.EventoNotFoundException;
import com.capgeticket.ventaEntradas.exception.InestableBankException;
import com.capgeticket.ventaEntradas.response.BancoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

import static feign.FeignException.errorStatus;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        BancoResponse resp = null;
        if(response.status() == 404) {
            return new EventoNotFoundException("No se pudo encontrar el evento con id " + response.request().url());
        }
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            resp = mapper.readValue(bodyIs, BancoResponse.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        if(response.status() == 400) {
            return new BancoRejectedException(resp);
        }
        if(response.status() == 500) {
            return new InestableBankException(resp);
        }
        return errorStatus(methodKey, response);
    }
}
