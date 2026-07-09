package com.travelagency.reportservice.client;

import com.travelagency.reportservice.dto.PaymentResponse;
import com.travelagency.reportservice.exception.PaymentServiceUnavailableException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

// Cliente HTTP hacia payment-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class PaymentServiceClient {

    private static final String PAYMENT_SERVICE_ID = "payment-service";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public PaymentServiceClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    // Consulta masiva: evita una llamada HTTP por cada reserva del reporte
    public List<PaymentResponse> getPaymentsByBookingIds(List<Long> bookingIds) {
        if (bookingIds.isEmpty()) {
            return List.of();
        }

        String idsParam = bookingIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = UriComponentsBuilder.fromHttpUrl(resolveBaseUrl() + "/api/payments/by-bookings")
                .queryParam("bookingIds", idsParam)
                .toUriString();
        try {
            ResponseEntity<List<PaymentResponse>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<PaymentResponse>>() {
                    });
            List<PaymentResponse> body = response.getBody();
            return body != null ? body : List.of();
        } catch (RestClientException ex) {
            throw new PaymentServiceUnavailableException(
                    "No se pudo obtener los pagos desde payment-service: " + ex.getMessage(), ex);
        }
    }

    private String resolveBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(PAYMENT_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new PaymentServiceUnavailableException("No hay instancias de payment-service registradas en Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}
