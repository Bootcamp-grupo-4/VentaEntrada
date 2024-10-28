package com.capgeticket.ventaEntradas.service;

import com.capgeticket.ventaEntradas.dto.VentaEntradasDto;
import com.capgeticket.ventaEntradas.response.VentaEntradasResponseDto;

public interface VentaEntradasService {

    VentaEntradasResponseDto compra(VentaEntradasDto ventaEntradasDto);
}
