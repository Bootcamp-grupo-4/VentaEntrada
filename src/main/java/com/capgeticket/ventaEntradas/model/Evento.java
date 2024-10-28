package com.capgeticket.ventaEntradas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "fechaevento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "preciominimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMinimo;

    @Column(name = "preciomaximo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMaximo;

    @Column(name = "localidad", nullable = false, length = 255)
    private String localidad;

    @Column(name = "nombredelrecinto", nullable = false, length = 255)
    private String nombreDelRecinto;

    @Column(name = "genero", nullable = false, length = 255)
    private String genero;

    @Column(name = "mostrar", nullable = false)
    private Boolean mostrar;

    @OneToMany(mappedBy = "evento")
    private Set<VentaEntrada> ventaEntradas;

    public Evento(Long id, String nombre, String descripcion, LocalDate fechaEvento, BigDecimal precioMinimo, BigDecimal precioMaximo, String localidad, String nombreDelRecinto, String genero, Boolean mostrar) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaEvento = fechaEvento;
        this.precioMinimo = precioMinimo;
        this.precioMaximo = precioMaximo;
        this.localidad = localidad;
        this.nombreDelRecinto = nombreDelRecinto;
        this.genero = genero;
        this.mostrar = mostrar;
    }
}