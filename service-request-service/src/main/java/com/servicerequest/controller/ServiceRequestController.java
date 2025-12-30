package com.servicerequest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.model.UsedPart;
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestCreateDTO;
import com.servicerequest.requestdto.UpdateStatusDTO;
import com.servicerequest.responsedto.ServiceRequestResponse;
import com.servicerequest.service.ServiceRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

	@Autowired
	private ServiceRequestService service;
	

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> create(
            @Valid @RequestBody ServiceRequestCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRequest(dto));
    }
    
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<ServiceRequest>> customerRequests(@PathVariable String id){
        return ResponseEntity.ok(service.getCustomerRequests(id));
    }
    
    @PatchMapping("/{id}/assign")
    public Map<String,String> assign(@PathVariable String id,
                                     @Valid @RequestBody AssignTechnicianDTO dto){
        return Map.of("message", service.assignTechnician(id, dto));
    }
    
    @PatchMapping("/{id}/status")
    public Map<String,String> status(@PathVariable String id,
                                     @Valid @RequestBody UpdateStatusDTO dto){
        return Map.of("message", service.updateStatus(id, dto));
    }
    
    //Techncican requests parts
    @PostMapping("/{id}/parts/request")
    public Map<String,String> requestParts(@PathVariable String id,
            @RequestBody List<UsedPart> parts){
        return Map.of("message", service.requestParts(id, parts));
    }

    //Manager approves parts
    @PatchMapping("/{id}/parts/approve")
    public Map<String,String> approveParts(@PathVariable String id,
            @RequestParam String managerId){
        return Map.of("message", service.approveParts(id, managerId));
    }
    
    @GetMapping("/status/{status}")
    public List<ServiceRequest> getByStatus(@PathVariable ServiceStatus status){
        return service.getByStatus(status);
    }

    
    
}
