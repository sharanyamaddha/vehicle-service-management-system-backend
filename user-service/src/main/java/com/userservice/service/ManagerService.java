package com.userservice.service;

import java.util.List;

import com.userservice.model.Manager;
import com.userservice.requestdto.ManagerCreateRequest;

public interface ManagerService {

    Manager createManager(ManagerCreateRequest request);
    List<Manager> getAllManagers();
}
