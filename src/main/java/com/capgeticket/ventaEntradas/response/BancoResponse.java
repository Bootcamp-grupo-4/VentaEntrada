package com.capgeticket.ventaEntradas.response;

import com.capgeticket.ventaEntradas.dto.BancoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BancoResponse {

    private String timestamp;
    private String status;
    private String error;
    private List<String> message;
    private BancoDto info;
    private String infoadicional;
}
