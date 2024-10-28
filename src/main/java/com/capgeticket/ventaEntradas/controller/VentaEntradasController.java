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


    /**
     * Método que gestiona la compra de entradas. Recibe una solicitud POST con los datos de la venta encapsulados
     * en un objeto de tipo VentaEntradasDto, valida los datos recibidos, y llama al servicio para procesar la compra.
     * Si los datos son correctos, devuelve una respuesta con la información de la compra realizada.
     *
     * @param ventaEntradasDto Objeto que contiene los datos necesarios para realizar la compra de la entrada.
     * @return ResponseEntity con el estado de la solicitud y el cuerpo con la información de la compra realizada.
     * @throws IllegalArgumentException si el objeto VentaEntradasDto es nulo.
     */
    @PostMapping
    public ResponseEntity<VentaEntradasResponseDto> comprarEntrada(@RequestBody @Valid VentaEntradasDto ventaEntradasDto) {
        logger.info("Solicitud recibida para comprar una entrada.");

        if (ventaEntradasDto == null) {
            logger.error("El objeto VentaEntradasDto es nulo.");
            throw new IllegalArgumentException("VentaEntradasDto no puede ser nulo");
        }

        logger.debug("Datos de la compra recibidos: {}", ventaEntradasDto);

        VentaEntradasResponseDto compra = ventaEntradasService.compra(ventaEntradasDto);

        logger.info("Compra realizada exitosamente para el evento: {}", ventaEntradasDto.getEvento().getId());
        logger.debug("Respuesta de la compra: {}", compra);

        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }


}
