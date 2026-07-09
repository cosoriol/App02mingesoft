package com.travelagency.bookingservice.client;

import com.travelagency.bookingservice.dto.PackageResponse;
import com.travelagency.bookingservice.exception.PackageServiceUnavailableException;
import com.travelagency.bookingservice.exception.ResourceNotFoundException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// Cliente HTTP hacia package-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class BookingClient {

    private static final String PACKAGE_SERVICE_ID = "package-service";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public BookingClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    // Consulta un paquete puntual en package-service
    public PackageResponse getPackageById(Long packageId) {
        String url = resolveBaseUrl() + "/api/packages/" + packageId;
        try {
            return restTemplate.getForObject(url, PackageResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe un paquete con id " + packageId);
        } catch (RestClientException ex) {
            throw new PackageServiceUnavailableException(
                    "No se pudo obtener el paquete " + packageId + " desde package-service: " + ex.getMessage(), ex);
        }
    }

    // Ajusta los cupos reservados de un paquete: delta positivo reserva cupos, negativo los libera
    public PackageResponse updatePackageBookedSlots(Long packageId, int delta) {
        String url = resolveBaseUrl() + "/api/packages/" + packageId + "/slots?delta=" + delta;
        try {
            return restTemplate.exchange(url, HttpMethod.PATCH, null, PackageResponse.class).getBody();
        } catch (RestClientException ex) {
            throw new PackageServiceUnavailableException(
                    "No se pudo ajustar los cupos del paquete " + packageId + " en package-service: " + ex.getMessage(), ex);
        }
    }

    // Devuelve cupos liberados por una cancelacion o expiracion de reserva
    public PackageResponse releaseSlots(Long packageId, int count) {
        return updatePackageBookedSlots(packageId, -count);
    }

    // Resuelve host:puerto de una instancia registrada de package-service consultando a Eureka
    private String resolveBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(PACKAGE_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new PackageServiceUnavailableException("No hay instancias de package-service registradas en Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}
