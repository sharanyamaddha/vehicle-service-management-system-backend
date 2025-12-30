package com.vehicleservice.service;

import java.util.List;

import com.vehicleservice.requestdto.VehicleRequest;
import com.vehicleservice.responsedto.VehicleResponse;

public interface VehicleService {

    VehicleResponse addVehicle(VehicleRequest request);

    List<VehicleResponse> getVehiclesByCustomer(String userId);
    

    String deleteVehicle(String id);
}
