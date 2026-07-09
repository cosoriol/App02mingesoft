package com.travelagency.searchservice.controller;

import com.travelagency.searchservice.dto.PackageResponse;
import com.travelagency.searchservice.dto.SearchRequest;
import com.travelagency.searchservice.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Expone la busqueda y filtrado de paquetes turisticos (Epica 3)
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/by-destination")
    public List<PackageResponse> byDestination(@RequestParam String destination) {
        return searchService.searchByDestination(destination);
    }

    @GetMapping("/by-price")
    public List<PackageResponse> byPrice(@RequestParam("min") BigDecimal min, @RequestParam("max") BigDecimal max) {
        return searchService.searchByPrice(min, max);
    }

    @GetMapping("/by-date")
    public List<PackageResponse> byDate(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return searchService.searchByDateRange(startDate, endDate);
    }

    @GetMapping("/advanced")
    public List<PackageResponse> advanced(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) String travelType,
            @RequestParam(required = false) String season) {
        SearchRequest filters = new SearchRequest();
        filters.setDestination(destination);
        filters.setMinPrice(minPrice);
        filters.setMaxPrice(maxPrice);
        filters.setStartDate(startDate);
        filters.setTravelType(travelType);
        filters.setSeason(season);
        return searchService.searchAll(filters);
    }
}
