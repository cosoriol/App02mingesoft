package com.travelagency.reportservice.dto;

import java.util.List;

public class PackageRankingResponse {

    private final List<PackageRankingItem> items;
    private final PackageRankingSummary summary;

    public PackageRankingResponse(List<PackageRankingItem> items, PackageRankingSummary summary) {
        this.items = items;
        this.summary = summary;
    }

    public List<PackageRankingItem> getItems() {
        return items;
    }

    public PackageRankingSummary getSummary() {
        return summary;
    }
}
