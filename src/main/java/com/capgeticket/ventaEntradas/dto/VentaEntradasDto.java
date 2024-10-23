package com.capgeticket.ventaEntradas.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class VentaEntradasDto {


    private Long id;

    private EventoDto evento;

    private String nombreTitular;

    private String numeroTarjeta;

    private Integer mesCaducidad;

    private Integer yearCaducidad;

    private String concepto;

    private BigDecimal cantidad;

    private Integer cvv;

    private String emisor;

    private LocalDateTime fechaCompra;
}
