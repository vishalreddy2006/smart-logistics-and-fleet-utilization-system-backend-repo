package com.smartlogistics.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Trip;
import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.TripRepository;
import com.smartlogistics.repository.VehicleRepository;

@Service
public class AnalyticsService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    public AnalyticsService(TripRepository tripRepository, VehicleRepository vehicleRepository) {
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Map<String, Object> getTripActivity() {
        List<Trip> trips = tripRepository.findAll();
        List<Map<String, Object>> tripBuckets = buildTripCountTrend(trips);

        int totalTripsToday = tripBuckets.isEmpty()
                ? 0
                : ((Number) tripBuckets.get(tripBuckets.size() - 1).get("value")).intValue();

        double averageTripDistance = trips.stream()
                .map(Trip::getDistance)
                .filter(distance -> distance != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalTripsToday", totalTripsToday);
        result.put("averageTripDistance", round2(averageTripDistance));
        result.put("tripsPerDay", tripBuckets);
        return result;
    }

    public Map<String, Object> getFuelUsage() {
        List<Trip> trips = tripRepository.findAll();

        double totalFuelConsumed = trips.stream()
                .mapToDouble(Trip::getPredictedFuel)
                .sum();

        double averageFuelPerTrip = trips.isEmpty() ? 0.0 : totalFuelConsumed / trips.size();

        Map<LocalDate, List<Trip>> tripsByDate = distributeTripsAcrossLast7Days(trips);
        List<Map<String, Object>> fuelUsageTrend = new ArrayList<>();

        for (LocalDate date : sortedDates(tripsByDate)) {
            double fuel = tripsByDate.get(date).stream()
                    .mapToDouble(Trip::getPredictedFuel)
                    .sum();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)));
            item.put("value", round2(fuel));
            fuelUsageTrend.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalFuelConsumed", round2(totalFuelConsumed));
        result.put("averageFuelPerTrip", round2(averageFuelPerTrip));
        result.put("fuelUsageTrend", fuelUsageTrend);
        return result;
    }

    public Map<String, Object> getProfitAnalysis() {
        List<Trip> trips = tripRepository.findAll();

        double totalRevenue = 0.0;
        double totalFuelCost = 0.0;
        double totalMaintenanceCost = 0.0;

        for (Trip trip : trips) {
            Double tripDistance = trip.getDistance();
            Double tripLoadWeight = trip.getLoadWeight();

            double distance = tripDistance == null ? 0.0 : tripDistance;
            double loadWeight = tripLoadWeight == null ? 0.0 : tripLoadWeight;
            double fuel = trip.getPredictedFuel();

            double tripRevenue = (distance * 45.0) + (loadWeight * 1.2);
            double fuelCost = fuel * 105.0;
            double maintenanceCost = estimateMaintenanceCost(trip.getVehicle());

            totalRevenue += tripRevenue;
            totalFuelCost += fuelCost;
            totalMaintenanceCost += maintenanceCost;
        }

        double totalCost = totalFuelCost + totalMaintenanceCost;
        double netProfit = totalRevenue - totalCost;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRevenue", round2(totalRevenue));
        result.put("totalCost", round2(totalCost));
        result.put("netProfit", round2(netProfit));
        return result;
    }

    public Map<String, Object> getCarbonEmissions() {
        List<Trip> trips = tripRepository.findAll();

        double totalEmission = trips.stream()
                .mapToDouble(Trip::getCarbonEmission)
                .sum();

        double averageEmissionPerTrip = trips.isEmpty() ? 0.0 : totalEmission / trips.size();

        Map<LocalDate, List<Trip>> tripsByDate = distributeTripsAcrossLast7Days(trips);
        List<Map<String, Object>> emissionTrend = new ArrayList<>();

        for (LocalDate date : sortedDates(tripsByDate)) {
            double emission = tripsByDate.get(date).stream()
                    .mapToDouble(Trip::getCarbonEmission)
                    .sum();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)));
            item.put("value", round2(emission));
            emissionTrend.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalEmission", round2(totalEmission));
        result.put("averageEmissionPerTrip", round2(averageEmissionPerTrip));
        result.put("emissionTrend", emissionTrend);
        return result;
    }

    public Map<String, Object> getFleetUtilization() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        int availableVehicles = 0;
        int vehiclesInTrip = 0;
        int vehiclesUnderMaintenance = 0;

        for (Vehicle vehicle : vehicles) {
            String status = vehicle.getStatus() == null ? "" : vehicle.getStatus().trim().toUpperCase();

            switch (status) {
                case "AVAILABLE" -> availableVehicles++;
                case "IN_TRIP", "IN_USE", "BUSY" -> vehiclesInTrip++;
                case "MAINTENANCE", "UNDER_MAINTENANCE" -> vehiclesUnderMaintenance++;
                default -> {
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("availableVehicles", availableVehicles);
        result.put("vehiclesInTrip", vehiclesInTrip);
        result.put("vehiclesUnderMaintenance", vehiclesUnderMaintenance);
        return result;
    }

    public Map<String, Object> getVehiclePerformance() {
        List<Trip> trips = tripRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        Map<Long, Vehicle> vehiclesById = new HashMap<>();
        for (Vehicle vehicle : vehicles) {
            vehiclesById.put(vehicle.getVehicleId(), vehicle);
        }

        Map<Long, Integer> tripCountByVehicle = new HashMap<>();
        Map<Long, Double> fuelByVehicle = new HashMap<>();

        for (Trip trip : trips) {
            if (trip.getVehicle() == null || trip.getVehicle().getVehicleId() == null) {
                continue;
            }

            Long vehicleId = trip.getVehicle().getVehicleId();
            tripCountByVehicle.put(vehicleId, tripCountByVehicle.getOrDefault(vehicleId, 0) + 1);
            fuelByVehicle.put(vehicleId, fuelByVehicle.getOrDefault(vehicleId, 0.0) + trip.getPredictedFuel());
        }

        List<Map<String, Object>> tripCountPerVehicle = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : tripCountByVehicle.entrySet()) {
            Long vehicleId = entry.getKey();
            Vehicle vehicle = vehiclesById.get(vehicleId);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("vehicleId", vehicleId);
            item.put("vehicleType", vehicle != null ? vehicle.getVehicleType() : "Unknown");
            item.put("tripCount", entry.getValue());
            item.put("fuelUsed", round2(fuelByVehicle.getOrDefault(vehicleId, 0.0)));
            item.put("maintenanceRisk", vehicle != null ? vehicle.getMaintenanceRisk() : null);
            tripCountPerVehicle.add(item);
        }

        tripCountPerVehicle.sort((a, b) -> Integer.compare(
                ((Number) b.get("tripCount")).intValue(),
                ((Number) a.get("tripCount")).intValue()
        ));

        List<Map<String, Object>> topUsedVehicles = tripCountPerVehicle.stream()
                .limit(5)
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topUsedVehicles", topUsedVehicles);
        result.put("tripCountPerVehicle", tripCountPerVehicle);
        return result;
    }

    private List<Map<String, Object>> buildTripCountTrend(List<Trip> trips) {
        Map<LocalDate, List<Trip>> tripsByDate = distributeTripsAcrossLast7Days(trips);
        List<Map<String, Object>> trend = new ArrayList<>();

        for (LocalDate date : sortedDates(tripsByDate)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)));
            item.put("value", tripsByDate.get(date).size());
            trend.add(item);
        }

        return trend;
    }

    private Map<LocalDate, List<Trip>> distributeTripsAcrossLast7Days(List<Trip> trips) {
        List<Trip> sortedTrips = trips.stream()
                .sorted(Comparator.comparing(Trip::getTripId, Comparator.nullsLast(Long::compareTo)))
                .toList();

        Map<LocalDate, List<Trip>> grouped = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            grouped.put(today.minusDays(i), new ArrayList<>());
        }

        if (sortedTrips.isEmpty()) {
            return grouped;
        }

        List<LocalDate> dates = new ArrayList<>(grouped.keySet());
        for (int i = 0; i < sortedTrips.size(); i++) {
            Trip trip = sortedTrips.get(i);
            LocalDate date = dates.get(i % dates.size());
            grouped.get(date).add(trip);
        }

        return grouped;
    }

    private List<LocalDate> sortedDates(Map<LocalDate, List<Trip>> tripsByDate) {
        return tripsByDate.keySet().stream().sorted().toList();
    }

    private double estimateMaintenanceCost(Vehicle vehicle) {
        if (vehicle == null || vehicle.getMaintenanceRisk() == null) {
            return 150.0;
        }

        String risk = vehicle.getMaintenanceRisk().trim().toUpperCase();
        if ("HIGH".equals(risk)) {
            return 500.0;
        }
        if ("MEDIUM".equals(risk)) {
            return 300.0;
        }
        return 150.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
