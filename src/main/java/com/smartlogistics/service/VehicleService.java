package com.smartlogistics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.exception.VehicleInUseException;
import com.smartlogistics.repository.TripRepository;
import com.smartlogistics.repository.VehicleRepository;
import com.smartlogistics.util.UserContext;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final MaintenancePredictionService maintenancePredictionService;
    private final UserContext userContext;

    public VehicleService(VehicleRepository vehicleRepository,
                          TripRepository tripRepository,
                          MaintenancePredictionService maintenancePredictionService,
                          UserContext userContext) {
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.maintenancePredictionService = maintenancePredictionService;
        this.userContext = userContext;
    }

    public List<Vehicle> scanFleetMaintenance() {
        Long currentUserId = userContext.getCurrentUserIdOrNull();
        List<Vehicle> vehicles;

        // Filter by current user if authenticated
        if (currentUserId != null) {
            vehicles = vehicleRepository.findByUserId(currentUserId);
        } else {
            vehicles = vehicleRepository.findAll();
        }

        for (Vehicle vehicle : vehicles) {
            int age = vehicle.getAge() != null ? vehicle.getAge() : 0;
            double mileage = vehicle.getMileage() != null ? vehicle.getMileage() : 0.0;

            String maintenanceRisk = maintenancePredictionService.predictRisk(age, mileage);
            vehicle.setMaintenanceRisk(maintenanceRisk);
            vehicleRepository.save(vehicle);
        }

        return vehicles;
    }

    public void deleteVehicle(Long vehicleId) {
        Long currentUserId = userContext.getCurrentUserIdOrNull();

        // Check vehicle exists
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));

        // Verify ownership if user is authenticated
        if (currentUserId != null && !vehicle.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized access: You can only delete your own vehicles");
        }

        // Check if vehicle is in use
        if (tripRepository.existsByVehicleVehicleId(vehicleId)) {
            throw new VehicleInUseException("Vehicle cannot be deleted because it is assigned to existing trips.");
        }

        vehicleRepository.deleteById(vehicleId);
    }
}
