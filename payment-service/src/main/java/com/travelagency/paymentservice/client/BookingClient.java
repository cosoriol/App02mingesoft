package com.travelagency.paymentservice.client;

import com.travelagency.paymentservice.dto.BookingResponse;
import com.travelagency.paymentservice.exception.BookingServiceUnavailableException;
import com.travelagency.paymentservice.exception.ResourceNotFoundException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// Cliente HTTP hacia booking-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class BookingClient {

    private static final String BOOKING_SERVICE_ID = "booking-service";

    // booking-service exige un userId para validar pertenencia de la reserva. Esta es una
    // llamada de sistema (no la origina ningun usuario), asi que se usa el mismo valor
    // convencional de administrador que ya bypasea esa validacion en booking-service.
    private static final String SYSTEM_USER_ID = "admin";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public BookingClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    // Consulta una reserva puntual en booking-service
    public BookingResponse getBooking(Long bookingId) {
        String url = resolveBaseUrl() + "/api/bookings/" + bookingId + "?userId=" + SYSTEM_USER_ID;
        try {
            return restTemplate.getForObject(url, BookingResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe una reserva con id " + bookingId);
        } catch (RestClientException ex) {
            throw new BookingServiceUnavailableException(
                    "No se pudo obtener la reserva " + bookingId + " desde booking-service: " + ex.getMessage(), ex);
        }
    }

    // Confirma la reserva tras un pago exitoso
    public BookingResponse confirmBooking(Long bookingId) {
        String url = resolveBaseUrl() + "/api/bookings/" + bookingId + "/confirm";
        try {
            return restTemplate.exchange(url, HttpMethod.PATCH, null, BookingResponse.class).getBody();
        } catch (RestClientException ex) {
            throw new BookingServiceUnavailableException(
                    "No se pudo confirmar la reserva " + bookingId + " en booking-service: " + ex.getMessage(), ex);
        }
    }

    // Resuelve host:puerto de una instancia registrada de booking-service consultando a Eureka
    private String resolveBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(BOOKING_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new BookingServiceUnavailableException("No hay instancias de booking-service registradas en Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}
