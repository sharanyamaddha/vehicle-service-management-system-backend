package com.servicerequest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.servicerequest.enums.Priority;
import com.servicerequest.enums.ServiceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "service_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {

    @Id
    private String id;

    private String requestNumber;

    @NotBlank
    private String vehicleId;

    @NotBlank
    private String customerId;

    private String technicianId;

    private String bayId;

    @NotNull
    private Priority priority;

    private ServiceStatus status = ServiceStatus.REQUESTED;

    @NotBlank
    private String issue;
    
    @Version
    private Long version;


    private List<UsedPart> usedParts = new ArrayList<>();

    private PartsStatus partsStatus = PartsStatus.PARTS_NONE;

    private String partsRequestedBy;   // technicianId
    private String partsIssuedBy;      // managerId

    private LocalDateTime partsRequestedAt;
    private LocalDateTime partsIssuedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}
