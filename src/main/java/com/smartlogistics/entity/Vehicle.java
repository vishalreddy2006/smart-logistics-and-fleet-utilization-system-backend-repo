package com.smartlogistics.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    private static final Set<String> ALLOWED_MAINTENANCE_RISKS = Set.of("LOW", "MEDIUM", "HIGH");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    private String vehicleType;
    private Double capacity;
    private Double mileage;
    private Integer age;
    private String status;

    @Column(name = "maintenance_risk", length = 10)
    private String maintenanceRisk;

    public Vehicle() {
    }

    public Vehicle(Long vehicleId, String vehicleType, Double capacity, Double mileage, Integer age, String status) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
        this.mileage = mileage;
        this.age = age;
        this.status = status;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaintenanceRisk() {
        return maintenanceRisk;
    }

    public void setMaintenanceRisk(String maintenanceRisk) {
        if (maintenanceRisk == null || maintenanceRisk.isBlank()) {
            this.maintenanceRisk = null;
            return;
        }

        String normalizedRisk = maintenanceRisk.toUpperCase();
        if (!ALLOWED_MAINTENANCE_RISKS.contains(normalizedRisk)) {
            throw new IllegalArgumentException("maintenanceRisk must be one of: LOW, MEDIUM, HIGH");
        }

        this.maintenanceRisk = normalizedRisk;
    }
}
