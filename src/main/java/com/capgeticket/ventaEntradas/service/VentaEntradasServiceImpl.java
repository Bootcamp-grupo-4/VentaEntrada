package com.capgeticket.ventaEntradas.service;

import com.capgeticket.ventaEntradas.dto.BancoDto;
import com.capgeticket.ventaEntradas.dto.EventoDto;
import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.exception.EventoNotFoundException;
import com.capgeticket.ventaEntradas.feignClients.BancoFeignClient;
import com.capgeticket.ventaEntradas.model.VentaEntrada;
import com.capgeticket.ventaEntradas.repository.VentaEntradasRepository;
import com.capgeticket.ventaEntradas.feignClients.EventoFeignClient;
import com.capgeticket.ventaEntradas.response.BancoResponse;
import com.capgeticket.ventaEntradas.response.VentaEntradasResponseDto;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.capgeticket.ventaEntradas.mapper.EntradaMapper.ventaDtoMapper;
import static com.capgeticket.ventaEntradas.mapper.EntradaMapper.ventaMapper;
import static com.capgeticket.ventaEntradas.mapper.EventoMapper.eventoMapper;

@Service
public class VentaEntradasServiceImpl implements VentaEntradasService{

    private static final Logger logger = LoggerFactory.getLogger(VentaEntradasServiceImpl.class);

    @Autowired
    private VentaEntradasRepository ventaEntradasRepository;

    @Autowired
    private EventoFeignClient eventoFeignClient;

    @Autowired
    private BancoFeignClient bancoFeignClient;

    /**
     * Metodo que maneja la compra de entradas para los eventos
     * Primero se valida los datos de compra
     * Luego comprobamos que el evento para el que se quiere comprar la entrada existe
     * Una vez que se compruebe que el evento existe se envia al banco
     * Despues trataremos la respuesta del banco
     * - En caso de mensaje de error se anulara la compra y el usuario recibira el mensaje adecuado
     * - En caso de que el mensaje sea correcto la compra se llevara a cabo y se guardara en la base de datos
     *
     * @param ventaEntradasDto
     * @return devuelve un justificante de la compra en caso de que se haya podido realizar
     */
    @Override
    public VentaEntradasResponseDto compra(VentaEntradasDto ventaEntradasDto) {
        //Primero validamos la compra a nuestra manera
        validarEntrada(ventaEntradasDto);

        //Luego comprobamos que el evento para el que vamos a comprar entrada existe
        ResponseEntity<EventoDto> evento = eventoFeignClient.getEventoById(ventaEntradasDto.getEvento().getId());

        //Comprobar el status code
        if(evento.getStatusCode() != HttpStatus.OK) {
            logger.error("Me da a mi que este evento ya no existe");
            //Si no es OK se tira error
            throw new EventoNotFoundException(ventaEntradasDto.getEvento().getId());
        }

        if(evento.getBody() == null) {
            //Inventarme nombre de exception
            throw new IllegalArgumentException("Me da a mi que este evento ya existe");
        }

        //Enviamos al banco la petición
        BancoDto banco = BancoDto.of(ventaEntradasDto);
        ResponseEntity<BancoResponse> bancoResponse = null;
        try{
            bancoResponse = bancoFeignClient.pay(banco);
        } catch (FeignException.BadRequest e) {

        }
        //Tratamos la respuesta recibida
        if(bancoResponse != null && bancoResponse.getStatusCode() != HttpStatus.OK) {
            logger.error("Oooops, error en la compra");
            if(bancoResponse.getBody() != null) {
                handleBankResponse(bancoResponse.getStatusCode(), bancoResponse.getBody().getError(),bancoResponse.getBody().getMessage());
            }
        }
        //Si es correcta guardamos
        VentaEntrada venta = ventaMapper(ventaEntradasDto, eventoMapper(evento.getBody()));
        VentaEntradasDto ventaReturn = ventaDtoMapper(venta);
        VentaEntradasResponseDto response = new VentaEntradasResponseDto();
        response.setMensaje("Venta confirmada");
        response.setVenta(ventaReturn);
        return response;
    }

    /**
     * Valida los valores de compra dados por el usuario
     * @param ventaEntradasDto
     */
    private void validarEntrada(VentaEntradasDto ventaEntradasDto) {
        //tirar exception si esta chunga
    }

    private void handleBankResponse(HttpStatusCode statusCode, String error, List<String> message) {
        if(statusCode.is4xxClientError()) {

        }
        //Inestable
    }
}
