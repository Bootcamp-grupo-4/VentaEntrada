package com.capgeticket.ventaEntradas.service;

import com.capgeticket.ventaEntradas.dto.BancoDto;
import com.capgeticket.ventaEntradas.dto.EventoDto;
import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.exception.EventoNotFoundException;
import com.capgeticket.ventaEntradas.feignClients.BancoFeignClient;
import com.capgeticket.ventaEntradas.model.Evento;
import com.capgeticket.ventaEntradas.model.VentaEntrada;
import com.capgeticket.ventaEntradas.repository.VentaEntradasRepository;
import com.capgeticket.ventaEntradas.feignClients.EventoFeignClient;
import com.capgeticket.ventaEntradas.response.BancoResponse;
import com.capgeticket.ventaEntradas.response.VentaEntradasResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

        if(evento.getBody() != null) {
            //Inventarme nombre de exception
            throw new IllegalArgumentException("Me da a mi que este evento ya existe");
        }

        //Enviamos al banco la petición
        BancoDto banco = BancoDto.of(ventaEntradasDto);
        ResponseEntity<BancoResponse> bancoResponse = bancoFeignClient.pay(banco);
        //Tratamos la respuesta recibida
        if(bancoResponse.getStatusCode() != HttpStatus.OK) {
            logger.error("Oooops, error en la compra");
            if(bancoResponse.getBody() != null) {
                handleBankResponse(bancoResponse.getStatusCode(), bancoResponse.getBody().getError(),bancoResponse.getBody().getMessage());
            }
        }
        //Si es correcta guardamos
        VentaEntrada venta = ventaMapper(ventaEntradasDto,eventoMapper(evento.getBody()));
        VentaEntradasDto ventaReturn = ventaDtoMapper(venta);
        VentaEntradasResponseDto response = new VentaEntradasResponseDto();
        response.setMensaje("Venta confirmada");
        response.setVenta(ventaReturn);
        return null;
    }

    private VentaEntradasDto ventaDtoMapper(VentaEntrada venta) {
        VentaEntradasDto ret = new VentaEntradasDto();
        ret.setEvento(eventoDtoMapper(venta.getEvento()));
        ret.setNombreTitular(venta.getNombreTitular());
        ret.setNumeroTarjeta(venta.getNumeroTarjeta());
        ret.setMesCaducidad(venta.getMesCaducidad());
        ret.setYearCaducidad(venta.getYearCaducidad());
        ret.setConcepto(venta.getConcepto());
        ret.setCantidad(venta.getCantidad());
        ret.setFechaCompra(LocalDateTime.now());
        return ret;
    }

    private VentaEntrada ventaMapper(VentaEntradasDto ventaEntradasDto, Evento evento) {
        VentaEntrada e = new VentaEntrada();
        e.setEvento(evento);
        e.setNombreTitular(ventaEntradasDto.getNombreTitular());
        e.setNumeroTarjeta(ventaEntradasDto.getNumeroTarjeta());
        e.setMesCaducidad(ventaEntradasDto.getMesCaducidad());
        e.setYearCaducidad(ventaEntradasDto.getYearCaducidad());
        e.setConcepto(ventaEntradasDto.getConcepto());
        e.setCantidad(ventaEntradasDto.getCantidad());
        e.setFechaCompra(LocalDateTime.now());
        return e;
    }

    private void validarEntrada(VentaEntradasDto ventaEntradasDto) {
        //tirar exception si esta chunga
    }

    /**
     * Un mappeador para la entidad del evento a la hora de guardar la venta
     * Se que suena un poco más complicado pero en mi cabeza tiene sentido (Probablemente pueda hacerlo solo con el id, pero bueno por si acaso)
     * @param body el body de la petición del evento
     * @return devuelve un objeto de tipo Evento
     */
    private Evento eventoMapper(EventoDto body) {
        Evento e = new Evento();
        e.setId(body.getId());
        e.setNombre(body.getNombre());
        e.setDescripcion(body.getDescripcion());
        e.setFechaEvento(body.getFechaEvento());
        e.setPrecioMaximo(body.getPrecioMaximo());
        e.setPrecioMinimo(body.getPrecioMinimo());
        e.setLocalidad(body.getLocalidad());
        e.setNombreDelRecinto(body.getNombreDelRecinto());
        e.setGenero(body.getGenero());
        e.setMostrar(body.getMostrar());
        return e;
    }

    private EventoDto eventoDtoMapper(Evento evento) {
        EventoDto eventoDto = new EventoDto();
        eventoDto.setId(evento.getId());
        eventoDto.setNombre(evento.getNombre());
        eventoDto.setDescripcion(evento.getDescripcion());
        eventoDto.setFechaEvento(evento.getFechaEvento());
        eventoDto.setPrecioMaximo(evento.getPrecioMaximo());
        eventoDto.setPrecioMinimo(evento.getPrecioMinimo());
        eventoDto.setLocalidad(evento.getLocalidad());
        eventoDto.setNombreDelRecinto(evento.getNombreDelRecinto());
        eventoDto.setGenero(evento.getGenero());
        eventoDto.setMostrar(evento.getMostrar());
        return eventoDto;
    }

    private void handleBankResponse(HttpStatusCode statusCode, String error, List<String> message) {
        if(statusCode.is4xxClientError()) {

        }
        //Inestable
    }
}
