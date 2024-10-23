package com.capgeticket.ventaEntradas.response;

import com.capgeticket.ventaEntradas.dto.BancoDto;
import lombok.Data;

import java.util.List;

@Data
public class BancoResponse {

    private String timestamp;
    private String status;
    private String error;
    private List<String> message;
    private BancoDto info;
    private String infoAdicional;
}
