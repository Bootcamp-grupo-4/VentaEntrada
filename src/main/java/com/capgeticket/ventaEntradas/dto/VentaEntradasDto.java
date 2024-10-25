package com.capgeticket.ventaEntradas.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VentaEntradasDto {


    private Long id;

    private EventoDto evento;

    private String correoTitular;

    private String nombreTitular;

    private String numeroTarjeta;

    private Integer mesCaducidad;

    private Integer yearCaducidad;

    private String concepto;

    private BigDecimal cantidad;

    private Integer cvv;

    private String emisor;

    private LocalDateTime fechaCompra;

    public VentaEntradasDto(Long id, String nombreTitular, String numeroTarjeta, Integer mesCaducidad, Integer yearCaducidad, String concepto, BigDecimal cantidad, Integer cvv, String emisor, LocalDateTime fechaCompra, String correoTitular) {
        this.id = id;
        this.nombreTitular = nombreTitular;
        this.numeroTarjeta = numeroTarjeta;
        this.mesCaducidad = mesCaducidad;
        this.yearCaducidad = yearCaducidad;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.cvv = cvv;
        this.emisor = emisor;
        this.fechaCompra = fechaCompra;
        this.correoTitular = correoTitular;
    }
}
