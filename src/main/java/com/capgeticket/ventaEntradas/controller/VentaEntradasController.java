package com.capgeticket.ventaEntradas.controller;

import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.response.VentaEntradasResponseDto;
import com.capgeticket.ventaEntradas.service.VentaEntradasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ventaentradas")
@Tag(name="Compra", description = "Api compra")
public class VentaEntradasController {

    private static final Logger logger = LoggerFactory.getLogger(VentaEntradasController.class);

    @Autowired
    private VentaEntradasService ventaEntradasService;

    @PostMapping
    public ResponseEntity<VentaEntradasResponseDto> comprarEntrada(@RequestBody @Valid VentaEntradasDto ventaEntradasDto) {
        if(ventaEntradasDto == null) {
            throw new IllegalArgumentException("VentaEntradasDto cannot be null");
        }
        VentaEntradasResponseDto compra = ventaEntradasService.compra(ventaEntradasDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }

}
