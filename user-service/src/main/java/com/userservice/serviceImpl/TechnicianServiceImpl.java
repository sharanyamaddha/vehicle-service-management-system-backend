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
public class TechnicianServiceImpl implements TechnicianService {

    @Autowired
    private TechnicianRepository techRepository;

    @Override
    public TechnicianResponse createTechnician(TechnicianCreateRequest req){

        Technician t = new Technician();
        t.setUserId(req.getUserId());
        t.setSpecialization(req.getSpecialization());
        t.setAvailable(true);

        return map(techRepository.save(t));
    }

    @Override
    public List<TechnicianResponse> getTechniciansByStatus(boolean status){
        return techRepository.findByAvailable(status)
                .stream().map(this::map).toList();
    }
    
    @Override
    public List<TechnicianResponse> getAvailableBySpecialization(String Specialization){
        return techRepository
            .findBySpecializationAndAvailable(Specialization, true)
            .stream().map(this::map).toList();
    }


    private TechnicianResponse map(Technician t){
        TechnicianResponse r = new TechnicianResponse();
        r.setId(t.getId());
        r.setUserId(t.getUserId());
        r.setSpecialization(t.getSpecialization());
        r.setAvailable(t.isAvailable());
        return r;
    }
    
    @Override
    public void incrementWorkload(String techId) {

        Technician tech = techRepository.findById(techId)
            .orElseThrow(() -> new RuntimeException("Technician not found"));

        tech.setCurrentJobs(tech.getCurrentJobs() + 1);
        techRepository.save(tech);
    }

    @Override
    public void decrementWorkload(String techId) {

        Technician tech = techRepository.findById(techId)
            .orElseThrow(() -> new RuntimeException("Technician not found"));

        tech.setCurrentJobs(Math.max(0, tech.getCurrentJobs() - 1));
        techRepository.save(tech);
    }

}
