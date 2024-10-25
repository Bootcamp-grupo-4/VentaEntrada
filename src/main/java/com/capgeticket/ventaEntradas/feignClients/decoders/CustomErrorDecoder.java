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
            String err = resp.getError().toString();
            String[] codeA = err.split(".");
            if (err.equals("400.0001.Sin fondos")) {
                return new BancoRejectedException("No tienes fondos en la cuenta, por favor recarga el saldo de tu cuenta");
            } else if (err.equals("400.0002.Usuario no encontrado")) {
                return new BancoRejectedException("No se han podido encontrar tus datos en el banco, revise los datos enviados");
            } else if (err.equals("400.0003.Número de tarjeta no correcto")) {
                return new BancoRejectedException("El número de tarjeta introducido no es correcto, revise los datos enviados");
            } else if (err.equals("400.0004.Número Cvv no correcto")) {
                return new BancoRejectedException("El formato del CVV no es correcto, revise los datos enviados");
            } else if (err.equals("400.0005.Mes no correcto")) {
                return new BancoRejectedException("El mes de caducidad no es correcto, revise los datos enviados");
            } else if (err.equals("400.0006.Año no correcto")) {
                return new BancoRejectedException("El año de caducidad no es correcto, revise los datos enviados");
            } else if (err.equals("400.0007.Fecha Caducidad no posterior")) {
                return new BancoRejectedException("La fecha de caducidad introducida es anterior a hoy, revise los datos enviados");
            } else if (err.equals("400.0008.Nombre no correcto")) {
                return new BancoRejectedException("El nombre introducido no es correcto, revise los datos enviados");
            }
        }
        if(response.status() == 500) {
            return new InestableBankException("Ha ocurrido un error con el banco, pruebe de nuevo en un momento");
        }
        return errorStatus(methodKey, response);
    }
}
