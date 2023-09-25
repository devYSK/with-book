package com.apress.springrecipes.nosql;

import java.util.List;

import com.mongodb.MongoClient;

public class Main {

    public static final String DB_NAME = "vehicledb";

    public static void main(String[] args) throws Exception {
        // 기본 호스트 및 포트는 각각 localhost, 27017입니다.
        MongoClient mongo = new MongoClient();

        VehicleRepository repository = new MongoDBVehicleRepository(mongo, DB_NAME, "vehicles");

        System.out.println("Number of Vehicles: " + repository.count());

        repository.save(new Vehicle("TEM0001", "RED", 4, 4));
        repository.save(new Vehicle("TEM0002", "RED", 4, 4));

        System.out.println("Number of Vehicles: " + repository.count());

        Vehicle v = repository.findByVehicleNo("TEM0001");

        System.out.println(v);

        List<Vehicle> vehicleList = repository.findAll();

        System.out.println("Number of Vehicles: " + vehicleList.size());
        vehicleList.forEach(System.out::println);
        System.out.println("Number of Vehicles: " + repository.count());

        // DB를 삭제하고 인스턴스를 닫습니다.
        mongo.dropDatabase(DB_NAME);
        mongo.close();
    }
}
