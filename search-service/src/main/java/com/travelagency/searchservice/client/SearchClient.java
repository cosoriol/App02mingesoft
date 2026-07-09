package com.travelagency.searchservice.client;

import com.travelagency.searchservice.dto.PackageResponse;
import com.travelagency.searchservice.exception.PackageServiceUnavailableException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Cliente HTTP hacia package-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class SearchClient {

    private static final String PACKAGE_SERVICE_ID = "package-service";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public SearchClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    // Trae todos los paquetes con estado AVAILABLE segun package-service
    public List<PackageResponse> getAvailablePackages() {
        String url = resolveBaseUrl() + "/api/packages/available";
        return exchange(url);
    }

    // Delega la busqueda avanzada al endpoint de package-service, propagando los filtros opcionales
    public List<PackageResponse> searchPackages(String destination, BigDecimal minPrice, BigDecimal maxPrice, LocalDate startDate) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resolveBaseUrl() + "/api/packages/search");
        if (destination != null) {
            builder.queryParam("destination", destination);
        }
        if (minPrice != null) {
            builder.queryParam("minPrice", minPrice);
        }
        if (maxPrice != null) {
            builder.queryParam("maxPrice", maxPrice);
        }
        if (startDate != null) {
            builder.queryParam("startDate", startDate);
        }
        return exchange(builder.toUriString());
    }

    private List<PackageResponse> exchange(String url) {
        try {
            ResponseEntity<List<PackageResponse>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<PackageResponse>>() {
                    });
            List<PackageResponse> body = response.getBody();
            return body != null ? body : List.of();
        } catch (RestClientException ex) {
            throw new PackageServiceUnavailableException(
                    "No se pudo obtener respuesta de package-service: " + ex.getMessage(), ex);
        }
    }

    // Resuelve host:puerto de una instancia registrada de package-service consultando a Eureka
    private String resolveBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(PACKAGE_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new PackageServiceUnavailableException(
                    "No hay instancias de package-service registradas en Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}
