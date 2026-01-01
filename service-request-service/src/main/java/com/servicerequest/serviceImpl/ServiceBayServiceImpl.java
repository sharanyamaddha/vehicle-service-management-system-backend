package com.servicerequest.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.servicerequest.model.ServiceBay;
import com.servicerequest.repository.ServiceBayRepository;
import com.servicerequest.service.ServiceBayService;

@Service
public class ServiceBayServiceImpl implements ServiceBayService{

	@Autowired
	private ServiceBayRepository bayRepo;
	
    @Override
    public ServiceBay createBay(ServiceBay bay){
        return bayRepo.save(bay);
    }

    @Override
    public List<ServiceBay> getAvailableBays(){
        return bayRepo.findByAvailableTrue();
    }
	
    @Override
    public void occupyBay(int bayNumber) {
    	ServiceBay bay=bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Bay not found"));

    	if(!bay.isAvailable())
    		throw new RuntimeException("Bay already occupied");
    	bay.setAvailable(false);
    	bayRepo.save(bay);
    }
    
    @Override
    public void releaseBay(int bayNumber) {
    	ServiceBay bay=bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Bay not found"));

    	bay.setAvailable(true);
    	bayRepo.save(bay);
    			
    }

    @Override
    public ServiceBay findByBayNumber(int bayNumber) {

        ServiceBay bay = bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Service bay not found with number: " + bayNumber));

        if (!bay.isAvailable()) {
            throw new RuntimeException("Service bay " + bayNumber + " is already occupied");
        }

        return bay;
    }




}
