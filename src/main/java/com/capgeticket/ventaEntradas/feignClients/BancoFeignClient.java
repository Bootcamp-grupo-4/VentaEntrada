package com.capgeticket.ventaEntradas.feignClients;

import com.capgeticket.ventaEntradas.dto.BancoDto;
import com.capgeticket.ventaEntradas.response.BancoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="banco", url="http://banco.eu-west-3.elasticbeanstalk.com/")
public interface BancoFeignClient {

    @PostMapping("/pasarela/compra")
    public ResponseEntity<BancoResponse> pay(@RequestBody BancoDto body);
}
