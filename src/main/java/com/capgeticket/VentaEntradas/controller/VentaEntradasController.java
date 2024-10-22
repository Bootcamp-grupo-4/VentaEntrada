package com.capgeticket.VentaEntradas.controller;

import com.capgeticket.VentaEntradas.dto.VentaEntradasDto;
import com.capgeticket.VentaEntradas.service.VentaEntradasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/compra")
@Tag(name="Compra", description = "Api compra")
@Slf4j
public class VentaEntradasController {

    @Autowired
    private VentaEntradasService ventaEntradasService;

    @PostMapping
    public ResponseEntity<VentaEntradasDto> comprarEntrada(@RequestBody VentaEntradasDto ventaEntradasDto) {
        if(ventaEntradasDto == null) {
            throw new IllegalArgumentException("VentaEntradasDto cannot be null");
        }
        VentaEntradasDto compra = ventaEntradasService.compra(ventaEntradasDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }

}
