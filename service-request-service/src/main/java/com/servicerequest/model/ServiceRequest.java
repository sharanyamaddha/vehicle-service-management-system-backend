package com.servicerequest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import com.servicerequest.enums.PartsStatus;
import com.servicerequest.enums.Priority;
import com.servicerequest.enums.ServiceStatus;

import jakarta.validation.Valid;
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

    @NotBlank(message = "Request number is required")
    private String requestNumber;

    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String technicianId;

    @NotNull(message = "Bay number is required")
    private Integer bayNumber;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private ServiceStatus status = ServiceStatus.REQUESTED;

    @NotBlank(message = "Issue description is required")
    private String issue;

    @Version
    private Long version;

    @Valid
    private List<UsedPart> usedParts = new ArrayList<>();

    private PartsStatus partsStatus = PartsStatus.PARTS_NONE;

    private String partsRequestedBy;
    private String partsIssuedBy;

    private LocalDateTime partsRequestedAt;
    private LocalDateTime partsIssuedAt;

    @CreatedDate
    private LocalDateTime createdAt;
}
