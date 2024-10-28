package com.capgeticket.ventaEntradas.feignClients.configuration;

import com.capgeticket.ventaEntradas.feignClients.decoders.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomErrorConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
