package com.capgeticket.ventaEntradas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventoDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaEvento;
    private BigDecimal precioMinimo;
    private BigDecimal precioMaximo;
    private String localidad;
    private String nombreDelRecinto;
    private String genero;
    private Boolean mostrar;
}