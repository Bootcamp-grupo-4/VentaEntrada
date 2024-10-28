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

    /**
     * Método que decodifica la respuesta de error de un servicio externo y lanza la excepción apropiada basada en el código de estado HTTP
     * y el contenido del cuerpo de la respuesta.
     *
     * - Si el código de estado es 404, lanza una `EventoNotFoundException` con un mensaje que incluye la URL del evento no encontrado.
     * - Si el código de estado es 400, intenta leer el cuerpo de la respuesta, que es un objeto `BancoResponse`,
     *   y genera una excepción personalizada `BancoRejectedException` según el código de error devuelto por el banco.
     * - Si el código de estado es 500, lanza una `InestableBankException`, indicando problemas de inestabilidad en el servicio del banco.
     * - Si ningún caso coincide, delega el error al método por defecto `errorStatus`.
     *
     * @param methodKey Identificador de la solicitud HTTP, útil para el trazado y depuración.
     * @param response La respuesta HTTP que contiene el código de estado y el cuerpo del error.
     * @return Una excepción que será lanzada según el error ocurrido.
     */
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
            String err = resp.getError();
            System.out.println(err);
            String[] codeA = err.split(".");
            if (err.startsWith("400.0001")) {
                return new BancoRejectedException("0001", "No tienes fondos en la cuenta, por favor recarga el saldo de tu cuenta");
            } else if (err.startsWith("400.0002")) {
                return new BancoRejectedException("0002", "No se han podido encontrar tus datos en el banco, revise los datos enviados");
            } else if (err.startsWith("400.0003")) {
                return new BancoRejectedException("0003", "El número de tarjeta introducido no es correcto, revise los datos enviados");
            } else if (err.startsWith("400.0004")) {
                return new BancoRejectedException("0004", "El formato del CVV no es correcto, revise los datos enviados");
            } else if (err.startsWith("400.0005")) {
                return new BancoRejectedException("0005", "El mes de caducidad no es correcto, revise los datos enviados");
            } else if (err.startsWith("400.0006")) {
                return new BancoRejectedException("0006", "El año de caducidad no es correcto, revise los datos enviados");
            } else if (err.startsWith("400.0007")) {
                return new BancoRejectedException("0007", "La fecha de caducidad introducida es anterior a hoy, revise los datos enviados");
            } else if (err.startsWith("400.0008")) {
                return new BancoRejectedException("0008", "El nombre introducido no es correcto, revise los datos enviados");
            } else {
                return new BancoRejectedException("Error desconocido, contacte con los administradores");
            }
        }
        if(response.status() == 500) {
            return new InestableBankException("Ha ocurrido un error con el banco, pruebe de nuevo en un momento");
        }
        return errorStatus(methodKey, response);
    }
}
