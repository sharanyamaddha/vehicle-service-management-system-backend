package com.userservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userservice.model.Manager;
import com.userservice.repository.ManagerRepository;
import com.userservice.requestdto.ManagerCreateRequest;
import com.userservice.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository managerRepo;

    @Override
    public Manager createManager(ManagerCreateRequest req) {
        Manager m = new Manager();
        m.setUserId(req.getUserId());
        m.setBayGroup(req.getBayGroup());
        m.setMaxTechnicians(req.getMaxTechnicians());
        return managerRepo.save(m);
    }

    @Override
    public List<Manager> getAllManagers() {
        return managerRepo.findAll();
    }
}
