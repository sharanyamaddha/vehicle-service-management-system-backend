package com.servicerequest.service;

import java.util.List;

import com.servicerequest.model.ServiceBay;

public interface ServiceBayService {

    ServiceBay createBay(ServiceBay bay);

    List<ServiceBay> getAvailableBays();
    
    void occupyBay(int bayNumber);
    
    void releaseBay(int bayNUmber);

	//ServiceBay getBay(String bayId);


	ServiceBay findByBayNumber(int bayNumber);
}
