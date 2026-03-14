package com.smartlogistics.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.VehicleRepository;
import com.smartlogistics.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;

    public VehicleController(VehicleRepository vehicleRepository, VehicleService vehicleService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleService = vehicleService;
    }

    @PostMapping("/add")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);

        if (existingVehicle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle vehicleToUpdate = existingVehicle.get();
        vehicleToUpdate.setVehicleType(vehicle.getVehicleType());
        vehicleToUpdate.setCapacity(vehicle.getCapacity());
        vehicleToUpdate.setMileage(vehicle.getMileage());
        vehicleToUpdate.setAge(vehicle.getAge());
        vehicleToUpdate.setStatus(vehicle.getStatus());

        Vehicle updatedVehicle = vehicleRepository.save(vehicleToUpdate);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);

        if (existingVehicle.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Vehicle not found with id: " + id);
        }

        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Vehicle deleted successfully.");
    }

    @PostMapping("/run-maintenance-check")
    public ResponseEntity<List<Vehicle>> runMaintenanceCheck() {
        List<Vehicle> updatedVehicles = vehicleService.scanFleetMaintenance();
        return ResponseEntity.ok(updatedVehicles);
    }
}
