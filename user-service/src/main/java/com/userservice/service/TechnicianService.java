package com.userservice.service;

import java.util.List;

import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;

public interface TechnicianService {

	 TechnicianResponse createTechnician(TechnicianCreateRequest request);
	 
	    List<TechnicianResponse> getTechniciansByManager(String managerId);

	    List<TechnicianResponse> getTechniciansByStatus(boolean status);
}
