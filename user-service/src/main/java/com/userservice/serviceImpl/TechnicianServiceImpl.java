package com.userservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userservice.model.Technician;
import com.userservice.repository.TechnicianRepository;
import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;
import com.userservice.service.TechnicianService;

@Service
public class TechnicianServiceImpl implements TechnicianService{
	
    @Autowired
    private TechnicianRepository techRepository;

    @Override
    public TechnicianResponse createTechnician(TechnicianCreateRequest req) {
        Technician tech = new Technician();
        tech.setUserId(req.getUserId());
        tech.setSpecialization(req.getSpecialization());
        tech.setAvailable(req.isAvailable());
        tech.setManagerId(req.getManagerId());

        return mapToResponse(techRepository.save(tech));
    }
    

    @Override
    public List<TechnicianResponse> getTechniciansByManager(String managerId) {
        return techRepository.findByManagerId(managerId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<TechnicianResponse> getTechniciansByStatus(boolean status) {
        return techRepository.findByAvailable(status).stream().map(this::mapToResponse).toList();
    }
    
    
    private TechnicianResponse mapToResponse(Technician t){
        TechnicianResponse res = new TechnicianResponse();
        res.setId(t.getId());
        res.setUserId(t.getUserId());
        res.setSpecialization(t.getSpecialization());
        res.setAvailable(t.isAvailable());
        res.setManagerId(t.getManagerId());
        return res;
    }
}
