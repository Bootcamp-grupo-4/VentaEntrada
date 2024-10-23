package com.capgeticket.ventaEntradas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name="ventaEntrada")
public class VentaEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @Column(name = "nombreTitular", nullable = false, length = 255)
    private String nombreTitular;

    @Column(name = "numeroTarjeta", nullable = false, length = 255)
    private String numeroTarjeta;

    @Column(name = "mesCaducidad", nullable = false)
    private Integer mesCaducidad;

    @Column(name = "yearCaducidad", nullable = false)
    private Integer yearCaducidad;

    @Column(name = "concepto", nullable = false, length = 255)
    private String concepto;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "fechaCompra")
    private LocalDateTime fechaCompra;

}
