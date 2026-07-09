package com.travelagency.reportservice.client;

import com.travelagency.reportservice.dto.BookingResponse;
import com.travelagency.reportservice.exception.BookingServiceUnavailableException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

// Cliente HTTP hacia booking-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class BookingServiceClient {

    private static final String BOOKING_SERVICE_ID = "booking-service";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public BookingServiceClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    public List<BookingResponse> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        String url = UriComponentsBuilder.fromHttpUrl(resolveBaseUrl() + "/api/bookings/date-range")
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .toUriString();
        try {
            ResponseEntity<List<BookingResponse>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookingResponse>>() {
                    });
            List<BookingResponse> body = response.getBody();
            return body != null ? body : List.of();
        } catch (RestClientException ex) {
            throw new BookingServiceUnavailableException(
                    "No se pudo obtener las reservas del rango " + startDate + " - " + endDate
                            + " desde booking-service: " + ex.getMessage(), ex);
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
