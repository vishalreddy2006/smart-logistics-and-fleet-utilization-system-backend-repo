package com.smartlogistics.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlogistics.dto.RouteOptimizationResponse;
import com.smartlogistics.dto.RouteOptionResponse;

@Service
public class RouteOptimizationService {

    public RouteOptimizationResponse optimizeRoutes(String source, String destination) {
        List<RouteOptionResponse> routes = generateRoutes(source, destination);
        routes.sort(Comparator.comparingDouble(RouteOptionResponse::getRouteScore));

        RouteOptionResponse recommendedRoute = routes.get(0);
        return new RouteOptimizationResponse(routes, recommendedRoute);
    }

    private List<RouteOptionResponse> generateRoutes(String source, String destination) {
        double baseDistance = calculateBaseDistance(source, destination);

        String[] routeNames = {
                "Express Highway Route",
                "Urban Connector Route",
                "Eco Bypass Route"
        };

        double[] distanceMultipliers = {1.00, 1.12, 0.93};
        double[] averageSpeeds = {62.0, 48.0, 55.0};

        List<RouteOptionResponse> routes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double distance = round2(baseDistance * distanceMultipliers[i]);
            double estimatedTime = round2(distance / averageSpeeds[i]);
            double predictedFuel = round2(distance * 0.22);
            double carbonEmission = round2(predictedFuel * 2.68);
            double routeScore = round2((distance * 0.4) + (predictedFuel * 0.4) + (carbonEmission * 0.2));

            routes.add(new RouteOptionResponse(
                    routeNames[i],
                    distance,
                    estimatedTime,
                    predictedFuel,
                    carbonEmission,
                    routeScore
            ));
        }

        return routes;
    }

    private double calculateBaseDistance(String source, String destination) {
        int sourceFactor = Math.abs(source.trim().toLowerCase().hashCode() % 120);
        int destinationFactor = Math.abs(destination.trim().toLowerCase().hashCode() % 120);
        return 80.0 + ((sourceFactor + destinationFactor) / 2.0);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
