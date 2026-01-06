package com.servicerequest.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.servicerequest.model.ServiceBay;
import com.servicerequest.repository.ServiceBayRepository;
import com.servicerequest.service.ServiceBayService;

@Service
public class ServiceBayServiceImpl implements ServiceBayService {

    @Autowired
    private ServiceBayRepository bayRepo;

    @Override
    public ServiceBay createBay(ServiceBay bay) {
        if (bayRepo.findByBayNumber(bay.getBayNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bay number " + bay.getBayNumber() + " already exists.");
        }
        return bayRepo.save(bay);
    }

    @Override
    public List<ServiceBay> getAvailableBays() {
        return bayRepo.findByAvailableTrueAndActiveTrue();
    }

    @Override
    public void occupyBay(int bayNumber) {
        ServiceBay bay = bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Bay not found"));

        if (!bay.isAvailable())
            throw new RuntimeException("Bay already occupied");
        bay.setAvailable(false);
        bayRepo.save(bay);
    }

    @Override
    public void releaseBay(int bayNumber) {
        ServiceBay bay = bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Bay not found"));

        bay.setAvailable(true);
        bayRepo.save(bay);

    }

    @Override
    public List<ServiceBay> getAllBays() {
        return bayRepo.findAll();
    }

    @Override
    public void updateStatus(int bayNumber, boolean active) {
        ServiceBay bay = bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Bay not found"));
        bay.setActive(active);
        bayRepo.save(bay);
    }

    @Override
    public ServiceBay findByBayNumber(int bayNumber) {

        ServiceBay bay = bayRepo.findByBayNumber(bayNumber)
                .orElseThrow(() -> new RuntimeException("Service bay not found with number: " + bayNumber));

        if (!bay.isActive()) {
            throw new RuntimeException("Service bay " + bayNumber + " is disabled");
        }

        if (!bay.isAvailable()) {
            throw new RuntimeException("Service bay " + bayNumber + " is already occupied");
        }

        return bay;
    }

}
