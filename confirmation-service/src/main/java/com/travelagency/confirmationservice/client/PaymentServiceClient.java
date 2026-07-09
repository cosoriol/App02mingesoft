package com.travelagency.confirmationservice.client;

import com.travelagency.confirmationservice.dto.PaymentResponse;
import com.travelagency.confirmationservice.exception.PaymentServiceUnavailableException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

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

    // Vacio si la reserva todavia no tiene un pago registrado (no es un error, solo aun no se pago)
    public Optional<PaymentResponse> getPaymentByBookingId(Long bookingId) {
        String url = resolveBaseUrl() + "/api/payments/booking/" + bookingId;
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, PaymentResponse.class));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new PaymentServiceUnavailableException(
                    "No se pudo obtener el pago de la reserva " + bookingId + " desde payment-service: " + ex.getMessage(), ex);
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
