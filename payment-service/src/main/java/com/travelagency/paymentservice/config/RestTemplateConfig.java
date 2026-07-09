package com.travelagency.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // La fabrica de peticiones por defecto de RestTemplate usa HttpURLConnection, que no
    // soporta el metodo PATCH (limitacion historica del JDK). JdkClientHttpRequestFactory
    // usa java.net.http.HttpClient, que si lo soporta.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new JdkClientHttpRequestFactory());
    }
}
