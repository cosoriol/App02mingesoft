package com.travelagency.confirmationservice.client;

import com.travelagency.confirmationservice.dto.BookingResponse;
import com.travelagency.confirmationservice.exception.BookingServiceUnavailableException;
import com.travelagency.confirmationservice.exception.ResourceNotFoundException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// Cliente HTTP hacia booking-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class BookingServiceClient {

    private static final String BOOKING_SERVICE_ID = "booking-service";

    // booking-service exige un userId para validar pertenencia de la reserva. Las llamadas
    // de aqui que necesitan ver cualquier reserva (no una del propio usuario) usan el mismo
    // valor convencional de administrador que ya bypasea esa validacion en booking-service.
    private static final String SYSTEM_USER_ID = "admin";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public BookingServiceClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    public BookingResponse getBookingById(Long bookingId) {
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

    public List<BookingResponse> getBookingsByUserId(String userId) {
        String url = resolveBaseUrl() + "/api/bookings/user/" + userId;
        return exchangeList(url);
    }

    public List<BookingResponse> getAllBookings() {
        String url = resolveBaseUrl() + "/api/bookings?userId=" + SYSTEM_USER_ID;
        return exchangeList(url);
    }

    // Cancela la reserva en booking-service. effectiveUserId es "admin" cuando la cancelacion
    // la origina un administrador (bypasea la validacion de pertenencia en booking-service),
    // o el userId real del cliente cuando cancela su propia reserva.
    public BookingResponse cancelBooking(Long bookingId, String effectiveUserId) {
        String url = resolveBaseUrl() + "/api/bookings/" + bookingId + "/cancel?userId=" + effectiveUserId;
        try {
            return restTemplate.exchange(url, HttpMethod.PATCH, null, BookingResponse.class).getBody();
        } catch (RestClientException ex) {
            throw new BookingServiceUnavailableException(
                    "No se pudo cancelar la reserva " + bookingId + " en booking-service: " + ex.getMessage(), ex);
        }
    }

    private List<BookingResponse> exchangeList(String url) {
        try {
            ResponseEntity<List<BookingResponse>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookingResponse>>() {
                    });
            List<BookingResponse> body = response.getBody();
            return body != null ? body : List.of();
        } catch (RestClientException ex) {
            throw new BookingServiceUnavailableException(
                    "No se pudo obtener la lista de reservas desde booking-service: " + ex.getMessage(), ex);
        }
    }

    private String resolveBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(BOOKING_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new BookingServiceUnavailableException("No hay instancias de booking-service registradas en Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}
