package com.smartlogistics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Trip;
import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.TripRepository;
import com.smartlogistics.repository.VehicleRepository;
import com.smartlogistics.util.FuelPredictionService;
import com.smartlogistics.util.UserContext;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleRecommendationService vehicleRecommendationService;
    private final FuelPredictionService fuelPredictionService;
    private final CarbonEmissionService carbonEmissionService;
    private final UserContext userContext;

    public TripService(
            TripRepository tripRepository,
            VehicleRepository vehicleRepository,
            VehicleRecommendationService vehicleRecommendationService,
            FuelPredictionService fuelPredictionService,
            CarbonEmissionService carbonEmissionService,
            UserContext userContext
    ) {
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleRecommendationService = vehicleRecommendationService;
        this.fuelPredictionService = fuelPredictionService;
        this.carbonEmissionService = carbonEmissionService;
        this.userContext = userContext;
    }

    public Trip createTrip(Trip trip) {
        Long currentUserId = userContext.getCurrentUserIdOrNull();

        double distance = trip.getDistance() != null ? trip.getDistance() : 0.0;
        double loadWeight = trip.getLoadWeight() != null ? trip.getLoadWeight() : 0.0;

        // Resolve the vehicle: prefer user-selected vehicle; fall back to AI recommendation
        Vehicle assignedVehicle = null;

        if (trip.getVehicle() != null) {
            Long vehicleId = trip.getVehicle().getVehicleId();
            if (vehicleId != null) {
                assignedVehicle = vehicleRepository.findById(vehicleId).orElse(null);

                // Verify ownership if user is authenticated
                if (assignedVehicle != null && currentUserId != null
                        && (assignedVehicle.getUser() == null
                        || !assignedVehicle.getUser().getId().equals(currentUserId))) {

                    throw new RuntimeException("Unauthorized access: You can only create trips with your own vehicles");

                }
            }
        }

        if (assignedVehicle == null) {
            // Get available vehicles - filter by user if authenticated
            List<Vehicle> availableVehicles;
            if (currentUserId != null) {
                availableVehicles = vehicleRepository.findByUser_Id(currentUserId);
                availableVehicles = availableVehicles.stream()
                        .filter(v -> "AVAILABLE".equals(v.getStatus()))
                        .toList();
            } else {
                availableVehicles = vehicleRepository.findByStatus("AVAILABLE");
            }

            assignedVehicle = vehicleRecommendationService.recommendVehicle(distance, loadWeight, availableVehicles);
        }

        int vehicleAge = assignedVehicle.getAge() != null ? assignedVehicle.getAge() : 0;
        double predictedFuel = fuelPredictionService.predictFuel(distance, loadWeight, vehicleAge);
        double carbonEmission = carbonEmissionService.calculateEmission(predictedFuel);

        trip.setPredictedFuel(predictedFuel);
        trip.setCarbonEmission(carbonEmission);
        trip.setVehicle(assignedVehicle);

        // Set the current user as the owner
        if (currentUserId != null) {
            trip.setUserId(currentUserId);
        }

        return tripRepository.save(trip);
    }

    public List<Trip> getAllTrips() {
        Long currentUserId = userContext.getCurrentUserIdOrNull();

        // Filter by current user if authenticated
        if (currentUserId != null) {
            return tripRepository.findByUserId(currentUserId);
        } else {
            return tripRepository.findAll();
        }
    }
}
