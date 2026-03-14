package com.smartlogistics.dto;

public class RouteOptionResponse {

    private String routeName;
    private double distance;
    private double estimatedTime;
    private double predictedFuel;
    private double carbonEmission;
    private double routeScore;

    public RouteOptionResponse() {
    }

    public RouteOptionResponse(
            String routeName,
            double distance,
            double estimatedTime,
            double predictedFuel,
            double carbonEmission,
            double routeScore
    ) {
        this.routeName = routeName;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.predictedFuel = predictedFuel;
        this.carbonEmission = carbonEmission;
        this.routeScore = routeScore;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getPredictedFuel() {
        return predictedFuel;
    }

    public void setPredictedFuel(double predictedFuel) {
        this.predictedFuel = predictedFuel;
    }

    public double getCarbonEmission() {
        return carbonEmission;
    }

    public void setCarbonEmission(double carbonEmission) {
        this.carbonEmission = carbonEmission;
    }

    public double getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(double routeScore) {
        this.routeScore = routeScore;
    }
}
