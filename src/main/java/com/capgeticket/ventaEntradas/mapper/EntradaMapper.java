package com.capgeticket.ventaEntradas.mapper;

import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.model.Evento;
import com.capgeticket.ventaEntradas.model.VentaEntrada;

import java.time.LocalDateTime;

public class EntradaMapper {

    public static VentaEntradasDto ventaDtoMapper(VentaEntrada venta) {
        VentaEntradasDto ret = new VentaEntradasDto();
        ret.setId(venta.getId());
        ret.setEvento(EventoMapper.eventoDtoMapper(venta.getEvento()));
        ret.setNombreTitular(venta.getNombreTitular());
        ret.setNumeroTarjeta(venta.getNumeroTarjeta());
        ret.setMesCaducidad(venta.getMesCaducidad());
        ret.setYearCaducidad(venta.getYearCaducidad());
        ret.setConcepto(venta.getConcepto());
        ret.setCantidad(venta.getCantidad());
        ret.setFechaCompra(LocalDateTime.now());
        ret.setCorreoTitular(venta.getCorreoTitular());
        return ret;
    }

    public static VentaEntrada ventaMapper(VentaEntradasDto ventaEntradasDto, Evento evento) {
        VentaEntrada e = new VentaEntrada();
        e.setEvento(evento);
        e.setNombreTitular(ventaEntradasDto.getNombreTitular());
        e.setNumeroTarjeta(ventaEntradasDto.getNumeroTarjeta());
        e.setMesCaducidad(ventaEntradasDto.getMesCaducidad());
        e.setYearCaducidad(ventaEntradasDto.getYearCaducidad());
        e.setConcepto(ventaEntradasDto.getConcepto());
        e.setCantidad(ventaEntradasDto.getCantidad());
        e.setFechaCompra(LocalDateTime.now());
        e.setCorreoTitular(ventaEntradasDto.getCorreoTitular());
        return e;
    }
}
