package com.capgeticket.ventaEntradas.feignClients;

import com.capgeticket.ventaEntradas.dto.EventoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="evento", url="http://localhost:8081/")
public interface EventoFeignClient {

    @GetMapping("/evento/{id}")
    public ResponseEntity<EventoDto> getEventoById(@PathVariable("id") Long id);
}
