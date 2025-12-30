package com.vehicleservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vehicleservice.model.Vehicle;
import com.vehicleservice.repository.VehicleRepository;
import com.vehicleservice.requestdto.VehicleRequest;
import com.vehicleservice.responsedto.VehicleResponse;
import com.vehicleservice.service.VehicleService;

@Service
public class VehicleServiceImpl implements VehicleService{


    @Autowired
    private VehicleRepository vehicleRepo;
    
    @Override
    public VehicleResponse addVehicle(VehicleRequest req) {

        Vehicle v = new Vehicle();
        v.setOwnerId(req.getOwnerId());
        v.setRegistrationNumber(req.getRegistrationNumber());
        v.setMake(req.getMake());
        v.setModel(req.getModel());
        v.setYear(req.getYear());
        v.setType(req.getType());
        v.setColor(req.getColor());

        return mapToResponse(vehicleRepo.save(v));
    }
    

    @Override
    public List<VehicleResponse> getVehiclesByCustomer(String id) {
        return vehicleRepo.findByOwnerId(id).stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public String deleteVehicle(String id) {
        vehicleRepo.deleteById(id);
        return "Vehicle deleted";
    }
    
    
    private VehicleResponse mapToResponse(Vehicle v){
        VehicleResponse r = new VehicleResponse();
        r.setId(v.getId());
        r.setOwnerId(v.getOwnerId());
        r.setRegistrationNumber(v.getRegistrationNumber());
        r.setModel(v.getModel());
        r.setColor(v.getColor());
        r.setType(v.getType());	
        return r;
    }
}
