package com.smartlogistics.util;

import org.springframework.stereotype.Service;

/**
 * Utility service for estimating trip fuel consumption using a simple linear formula:
 * fuel = (distance * 0.05) + (loadWeight * 0.02) + (vehicleAge * 0.5)
 * where fuel is returned in liters.
 */
@Service
public class FuelPredictionService {

    /**
     * Predicts fuel consumption for a trip.
     *
     * @param distance the trip distance in kilometers
     * @param loadWeight the load weight in kilograms
     * @param vehicleAge the vehicle age in years
     * @return predicted fuel consumption in liters
     */
    public double predictFuel(double distance, double loadWeight, int vehicleAge) {
        return (distance * 0.05) + (loadWeight * 0.02) + (vehicleAge * 0.5);
    }
}
