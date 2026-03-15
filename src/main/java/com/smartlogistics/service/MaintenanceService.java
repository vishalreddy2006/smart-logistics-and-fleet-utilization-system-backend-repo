package com.smartlogistics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.VehicleRepository;

@Service
public class MaintenanceService {

    private final VehicleRepository vehicleRepository;

    public MaintenanceService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getMaintenanceData() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> predictMaintenance() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            int age = vehicle.getAge() != null ? vehicle.getAge() : 0;
            double mileage = vehicle.getMileage() != null ? vehicle.getMileage() : 0.0;

            double failureScore = (age * 10.0) + (mileage / 1000.0);
            String maintenanceRisk;

            if (failureScore > 70.0) {
                maintenanceRisk = "HIGH";
            } else if (failureScore >= 40.0) {
                maintenanceRisk = "MEDIUM";
            } else {
                maintenanceRisk = "LOW";
            }

            int daysLeftForService = (int) Math.max(0.0, 365.0 - (age * 60.0 + mileage / 200.0));

            vehicle.setFailureScore(failureScore);
            vehicle.setMaintenanceRisk(maintenanceRisk);
            vehicle.setDaysLeftForService(daysLeftForService);
            vehicleRepository.save(vehicle);
        }

        return vehicles;
    }
}
