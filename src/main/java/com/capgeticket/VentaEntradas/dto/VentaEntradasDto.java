package com.capgeticket.VentaEntradas.dto;


import com.capgeticket.VentaEntradas.model.Evento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

public class VentaEntradasDto {


    private Long id;

    private Evento evento;

    private String nombreTitular;

    private String numeroTarjeta;

    private BigDecimal mesCaducidad;

    private BigDecimal yearCaducidad;

    private String concepto;

    private BigDecimal cantidad;

    private Date fechaCompra;
}
