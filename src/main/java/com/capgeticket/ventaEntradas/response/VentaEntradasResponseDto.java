package com.capgeticket.ventaEntradas.response;
import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.model.Evento;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VentaEntradasResponseDto {


    private String mensaje;
    private VentaEntradasDto venta;
}
