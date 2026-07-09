package com.travelagency.reportservice.dto;

import java.util.List;

public class SalesReportResponse {

    private final List<SalesReportItem> items;
    private final SalesReportSummary summary;

    public SalesReportResponse(List<SalesReportItem> items, SalesReportSummary summary) {
        this.items = items;
        this.summary = summary;
    }

    public List<SalesReportItem> getItems() {
        return items;
    }

    public SalesReportSummary getSummary() {
        return summary;
    }
}
