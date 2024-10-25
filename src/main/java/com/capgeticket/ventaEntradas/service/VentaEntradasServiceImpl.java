package com.capgeticket.ventaEntradas.service;

import com.capgeticket.ventaEntradas.dto.BancoDto;
import com.capgeticket.ventaEntradas.dto.EventoDto;
import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.feignClients.BancoFeignClient;
import com.capgeticket.ventaEntradas.model.VentaEntrada;
import com.capgeticket.ventaEntradas.repository.VentaEntradasRepository;
import com.capgeticket.ventaEntradas.feignClients.EventoFeignClient;
import com.capgeticket.ventaEntradas.response.BancoResponse;
import com.capgeticket.ventaEntradas.response.VentaEntradasResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

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

        if(evento.getBody() == null) {
            //Inventarme nombre de exception
            throw new IllegalArgumentException("Hubo un error con la busqueda del evento");
        }

        BigDecimal min = evento.getBody().getPrecioMinimo();
        BigDecimal max = evento.getBody().getPrecioMaximo();

        ventaEntradasDto.setCantidad(BigDecimal.valueOf(min.doubleValue() + Math.random() * (max.doubleValue() - min.doubleValue())));
        ventaEntradasDto.setConcepto(evento.getBody().getNombre() + " " + evento.getBody().getNombreDelRecinto());

        //Enviamos al banco la petición
        BancoDto banco = BancoDto.of(ventaEntradasDto);
        ResponseEntity<BancoResponse> bancoResponse = bancoResponse = bancoFeignClient.pay(banco);
        //Si es correcta guardamos
        VentaEntrada venta = ventaMapper(ventaEntradasDto, eventoMapper(evento.getBody()));
        venta = ventaEntradasRepository.save(venta);
        VentaEntradasDto ventaReturn = ventaDtoMapper(venta);
        ventaReturn.setCvv(ventaEntradasDto.getCvv());
        ventaReturn.setEmisor(ventaEntradasDto.getEmisor());
        VentaEntradasResponseDto response = new VentaEntradasResponseDto();
        response.setMensaje("Venta confirmada");
        response.setVenta(ventaReturn);
        return response;
    }

    private void validarEntrada(VentaEntradasDto ventaEntradasDto) {
        if(ventaEntradasDto.getEvento() == null) {
            throw new IllegalArgumentException("El evento no puede estar vacio");
        }
        if(ventaEntradasDto.getEvento().getId() == null) {
            throw new IllegalArgumentException("El id de evento no puede ser nulo");
        }
        if(ventaEntradasDto.getNombreTitular() == null || ventaEntradasDto.getNombreTitular().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de titular no puede ser nulo o vacio");
        }
        if(ventaEntradasDto.getCorreoTitular() == null || ventaEntradasDto.getCorreoTitular().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo no puede ser nulo o vacio");
        }
        if(ventaEntradasDto.getNumeroTarjeta() == null || ventaEntradasDto.getNumeroTarjeta().trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de tarjeta no puede ser nulo o vacio");
        }
        if(ventaEntradasDto.getMesCaducidad() == null) {
            throw new IllegalArgumentException("El mes de caducidad no puede ser nulo");
        }
        if(ventaEntradasDto.getYearCaducidad() == null) {
            throw new IllegalArgumentException("El año de caducidad no puede ser nulo");
        }
        if(ventaEntradasDto.getCvv() == null) {
            throw new IllegalArgumentException("El CVV no puede ser nulo");
        }
        if(ventaEntradasDto.getEmisor() == null || ventaEntradasDto.getEmisor().trim().isEmpty()) {
            throw new IllegalArgumentException("El emisor no puede ser nulo o vacio");
        }
    }


}
