package com.travelagency.reportservice.client;

import com.travelagency.reportservice.dto.PackageResponse;
import com.travelagency.reportservice.exception.PackageServiceUnavailableException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// Cliente HTTP hacia package-service, resolviendo la instancia via Eureka (sin IPs/puertos fijos)
@Component
public class PackageServiceClient {

    private static final String PACKAGE_SERVICE_ID = "package-service";

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public PackageServiceClient(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    public PackageResponse getPackageById(Long packageId) {
        String url = resolveBaseUrl() + "/api/packages/" + packageId;
        try {
            return restTemplate.getForObject(url, PackageResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (RestClientException ex) {
            throw new PackageServiceUnavailableException(
                    "No se pudo obtener el paquete " + packageId + " desde package-service: " + ex.getMessage(), ex);
        }
    }

    private String resolveBaseUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(PACKAGE_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new PackageServiceUnavailableException("No hay instancias de package-service registradas en Eureka");
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}
