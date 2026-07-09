package com.travelagency.reportservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// report-service solo hace GET hacia los demas servicios, asi que el RestTemplate por
// defecto alcanza (a diferencia de booking-service/payment-service/confirmation-service,
// que necesitan JdkClientHttpRequestFactory para poder usar PATCH).
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
