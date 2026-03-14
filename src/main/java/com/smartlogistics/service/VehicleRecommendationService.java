package com.smartlogistics.service;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.exception.ResourceNotFoundException;
import com.smartlogistics.repository.VehicleRepository;
import com.smartlogistics.util.FuelPredictionService;

@Service
public class VehicleRecommendationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleRecommendationService.class);

    private final VehicleRepository vehicleRepository;
    private final FuelPredictionService fuelPredictionService;

    public VehicleRecommendationService(VehicleRepository vehicleRepository, FuelPredictionService fuelPredictionService) {
        this.vehicleRepository = vehicleRepository;
        this.fuelPredictionService = fuelPredictionService;
    }

    public Vehicle recommendVehicle(double distance, double loadWeight) {
        return recommendVehicle(distance, loadWeight, vehicleRepository.findAll());
    }

    public Vehicle recommendVehicle(double distance, double loadWeight, List<Vehicle> vehicles) {
        LOGGER.info("Vehicle recommendation requested | distance={} | loadWeight={} | inputVehicles={}",
            distance, loadWeight, vehicles.size());

        List<Vehicle> candidates = vehicles.stream()
                .filter(vehicle -> vehicle.getCapacity() != null && vehicle.getCapacity() >= loadWeight)
                .toList();

        LOGGER.info("Vehicles evaluated after capacity filter: {}", candidates.size());

        List<ScoredVehicle> scoredVehicles = candidates.stream()
            .map(vehicle -> {
                double score = calculateScore(vehicle, distance, loadWeight);
                LOGGER.debug("Evaluated vehicle | vehicleId={} | vehicleType={} | score={}",
                    vehicle.getVehicleId(), vehicle.getVehicleType(), score);
                return new ScoredVehicle(vehicle, score);
            })
            .toList();

        ScoredVehicle selected = scoredVehicles.stream()
            .max(Comparator.comparingDouble(ScoredVehicle::score))
            .orElseThrow(() -> new ResourceNotFoundException("No vehicle found with sufficient capacity for load weight: " + loadWeight));

        LOGGER.info("Selected vehicle | vehicleId={} | vehicleType={} | score={}",
            selected.vehicle().getVehicleId(), selected.vehicle().getVehicleType(), selected.score());

        return selected.vehicle();
    }

    private double calculateScore(Vehicle vehicle, double distance, double loadWeight) {
        Double capacityValue = vehicle.getCapacity();
        Double mileageValue = vehicle.getMileage();
        Integer ageValue = vehicle.getAge();

        double capacity = capacityValue != null ? capacityValue : 0.0;
        double mileage = mileageValue != null ? mileageValue : 0.0;
        int age = ageValue != null ? ageValue : 0;

        double predictedFuel = fuelPredictionService.predictFuel(distance, loadWeight, age);

        return (mileage * 0.5)
                + (capacity * 0.3)
                - (age * 0.2)
                - predictedFuel;
    }

    private record ScoredVehicle(Vehicle vehicle, double score) {
    }
}
