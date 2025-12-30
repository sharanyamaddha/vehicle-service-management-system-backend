package com.servicerequest.service;

import java.util.List;

import com.servicerequest.model.ServiceBay;

public interface ServiceBayService {

    ServiceBay createBay(ServiceBay bay);

    List<ServiceBay> getAvailableBays();
    
    void occupyBay(String bayId);
    
    void releaseBay(String bayId);
}
