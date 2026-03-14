package com.smartlogistics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.exception.VehicleInUseException;
import com.smartlogistics.repository.TripRepository;
import com.smartlogistics.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final MaintenancePredictionService maintenancePredictionService;

    public VehicleService(VehicleRepository vehicleRepository,
                          TripRepository tripRepository,
                          MaintenancePredictionService maintenancePredictionService) {
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.maintenancePredictionService = maintenancePredictionService;
    }

    public List<Vehicle> scanFleetMaintenance() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

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
        if (tripRepository.existsByVehicleVehicleId(vehicleId)) {
            throw new VehicleInUseException("Vehicle cannot be deleted because it is assigned to existing trips.");
        }

        vehicleRepository.deleteById(vehicleId);
    }
}
