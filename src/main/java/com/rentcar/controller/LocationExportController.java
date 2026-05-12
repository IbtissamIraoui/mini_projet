package com.rentcar.controller;

import com.rentcar.model.Location;
import com.rentcar.service.ExportService;
import com.rentcar.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/locations/export")
public class LocationExportController {

    private final LocationService locationService;
    private final ExportService exportService;

    @Autowired
    public LocationExportController(LocationService locationService, ExportService exportService) {
        this.locationService = locationService;
        this.exportService = exportService;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf() throws Exception {
        List<Location> locations = locationService.getAllLocations();
        byte[] pdf = exportService.exporterLocationsPdf(locations);
        String filename = "locations_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel() throws Exception {
        List<Location> locations = locationService.getAllLocations();
        byte[] excel = exportService.exporterLocationsExcel(locations);
        String filename = "locations_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
