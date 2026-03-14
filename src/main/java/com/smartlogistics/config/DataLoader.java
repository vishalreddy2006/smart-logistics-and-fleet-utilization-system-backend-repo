package com.smartlogistics.config;

import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final VehicleRepository vehicleRepository;

    public DataLoader(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void run(String... args) {
        if (vehicleRepository.count() > 0) {
            return;
        }

        vehicleRepository.save(new Vehicle(null, "Truck", 5000.0, 12.0, 3, "AVAILABLE"));
        vehicleRepository.save(new Vehicle(null, "Van", 2000.0, 15.0, 2, "AVAILABLE"));
        vehicleRepository.save(new Vehicle(null, "Mini Truck", 3000.0, 14.0, 4, "AVAILABLE"));
        vehicleRepository.save(new Vehicle(null, "Container Truck", 10000.0, 8.0, 5, "AVAILABLE"));
    }
}
