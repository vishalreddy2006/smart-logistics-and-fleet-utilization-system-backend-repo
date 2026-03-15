package com.smartlogistics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Trip;
import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.TripRepository;
import com.smartlogistics.repository.VehicleRepository;
import com.smartlogistics.util.FuelPredictionService;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleRecommendationService vehicleRecommendationService;
    private final FuelPredictionService fuelPredictionService;
    private final CarbonEmissionService carbonEmissionService;

    public TripService(
            TripRepository tripRepository,
            VehicleRepository vehicleRepository,
            VehicleRecommendationService vehicleRecommendationService,
            FuelPredictionService fuelPredictionService,
            CarbonEmissionService carbonEmissionService
    ) {
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleRecommendationService = vehicleRecommendationService;
        this.fuelPredictionService = fuelPredictionService;
        this.carbonEmissionService = carbonEmissionService;
    }

    public Trip createTrip(Trip trip) {
        double distance = trip.getDistance() != null ? trip.getDistance() : 0.0;
        double loadWeight = trip.getLoadWeight() != null ? trip.getLoadWeight() : 0.0;

        // Resolve the vehicle: prefer user-selected vehicle; fall back to AI recommendation
        Vehicle assignedVehicle = null;

        if (trip.getVehicle() != null) {
            Long vehicleId = trip.getVehicle().getVehicleId();
            if (vehicleId != null) {
                assignedVehicle = vehicleRepository.findById(vehicleId).orElse(null);
            }
        }

        if (assignedVehicle == null) {
            List<Vehicle> availableVehicles = vehicleRepository.findByStatus("AVAILABLE");
            assignedVehicle = vehicleRecommendationService.recommendVehicle(distance, loadWeight, availableVehicles);
        }

        int vehicleAge = assignedVehicle.getAge() != null ? assignedVehicle.getAge() : 0;
        double predictedFuel = fuelPredictionService.predictFuel(distance, loadWeight, vehicleAge);
        double carbonEmission = carbonEmissionService.calculateEmission(predictedFuel);

        trip.setPredictedFuel(predictedFuel);
        trip.setCarbonEmission(carbonEmission);
        trip.setVehicle(assignedVehicle);

        return tripRepository.save(trip);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }
}
