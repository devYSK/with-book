package com.apress.springrecipes.vehicle;

import java.util.Collection;
import java.util.List;

public interface VehicleDao {

    void insert(Vehicle vehicle);

    default void insert(Collection<Vehicle> vehicles) {
        vehicles.forEach(this::insert);
    }

    void update(Vehicle vehicle);

    void delete(Vehicle vehicle);

    Vehicle findByVehicleNo(String vehicleNo);

    List<Vehicle> findAll();

    default String getColor(String vehicleNo) {
        throw new IllegalStateException("Method is not implemented!");
    }

    default int countAll() {
        throw new IllegalStateException("Method is not implemented!");
    }

}
