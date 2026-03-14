package com.smartlogistics.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartlogistics.dto.MaintenanceAlertResponse;
import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.VehicleRepository;
import com.smartlogistics.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final VehicleRepository vehicleRepository;
    private final AnalyticsService analyticsService;

    public AnalyticsController(VehicleRepository vehicleRepository, AnalyticsService analyticsService) {
        this.vehicleRepository = vehicleRepository;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/maintenance-alerts")
    public ResponseEntity<List<MaintenanceAlertResponse>> getMaintenanceAlerts() {
        List<Vehicle> highRiskVehicles = vehicleRepository.findByMaintenanceRisk("HIGH");
        List<MaintenanceAlertResponse> response = highRiskVehicles.stream()
                .map(MaintenanceAlertResponse::fromVehicle)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/trip-activity")
    public ResponseEntity<Map<String, Object>> getTripActivity() {
        return ResponseEntity.ok(analyticsService.getTripActivity());
    }

    @GetMapping("/fuel-usage")
    public ResponseEntity<Map<String, Object>> getFuelUsage() {
        return ResponseEntity.ok(analyticsService.getFuelUsage());
    }

    @GetMapping("/profit-analysis")
    public ResponseEntity<Map<String, Object>> getProfitAnalysis() {
        return ResponseEntity.ok(analyticsService.getProfitAnalysis());
    }

    @GetMapping("/carbon-emissions")
    public ResponseEntity<Map<String, Object>> getCarbonEmissions() {
        return ResponseEntity.ok(analyticsService.getCarbonEmissions());
    }

    @GetMapping("/fleet-utilization")
    public ResponseEntity<Map<String, Object>> getFleetUtilization() {
        return ResponseEntity.ok(analyticsService.getFleetUtilization());
    }

    @GetMapping("/vehicle-performance")
    public ResponseEntity<Map<String, Object>> getVehiclePerformance() {
        return ResponseEntity.ok(analyticsService.getVehiclePerformance());
    }
}
