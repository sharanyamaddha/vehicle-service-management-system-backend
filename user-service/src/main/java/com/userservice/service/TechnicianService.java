package com.userservice.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;

public interface TechnicianService {

    TechnicianResponse createTechnician(TechnicianCreateRequest req);

    List<TechnicianResponse> getTechniciansByStatus(boolean status);
    
    List<TechnicianResponse> getAvailableBySpecialization(@PathVariable String specialization);
    
    void incrementWorkload(String techId);
    
    void decrementWorkload(String techId);

}
