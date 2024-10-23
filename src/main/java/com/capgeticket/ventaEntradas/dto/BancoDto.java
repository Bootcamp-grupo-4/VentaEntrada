package com.capgeticket.ventaEntradas.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BancoDto {

    private String nombreTitular;
    private String numeroTarjeta;
    private Integer mesCaducidad;
    private Integer yearCaducidad;
    private Integer cvv;
    private String emisor;
    private String concepto;
    private BigDecimal cantidad;

    public static BancoDto of(VentaEntradasDto ventaEntradasDto) {
        BancoDto bancoDto = new BancoDto();
        bancoDto.nombreTitular = ventaEntradasDto.getNombreTitular();
        bancoDto.numeroTarjeta = ventaEntradasDto.getNumeroTarjeta();
        bancoDto.mesCaducidad = ventaEntradasDto.getMesCaducidad();
        bancoDto.yearCaducidad = ventaEntradasDto.getYearCaducidad();
        bancoDto.cvv = ventaEntradasDto.getCvv();
        bancoDto.emisor = ventaEntradasDto.getEmisor();
        bancoDto.concepto = ventaEntradasDto.getConcepto();
        bancoDto.cantidad = ventaEntradasDto.getCantidad();
        return bancoDto;
    }
}
