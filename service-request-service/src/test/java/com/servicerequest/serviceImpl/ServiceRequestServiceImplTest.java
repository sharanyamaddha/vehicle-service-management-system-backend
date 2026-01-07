package com.servicerequest.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.servicerequest.FeignClient.InventoryClient;
import com.servicerequest.FeignClient.UserClient;
import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.model.Invoice;
import com.servicerequest.model.ServiceBay;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestDTO;
import com.servicerequest.requestdto.UpdateStatusDTO;
import com.servicerequest.responsedto.UserResponse;
import com.servicerequest.service.BillingService;
import com.servicerequest.service.ServiceBayService;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestServiceImplTest {

    @Mock
    private ServiceRequestRepository serviceReqRepo;

    @Mock
    private ServiceBayService bayService;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private BillingService billingService;

    @Mock
    private UserClient userClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ServiceRequestServiceImpl srService;

    private ServiceRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = new ServiceRequest();
        mockRequest.setId("req1");
        mockRequest.setVehicleId("v1");
        mockRequest.setCustomerId("u1");
        mockRequest.setStatus(ServiceStatus.REQUESTED);
    }

    @Test
    void createRequest_Success() {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setVehicleId("v1");
        dto.setCustomerId("u1");
        dto.setIssue("Engine issue");
        dto.setPriority(com.servicerequest.enums.Priority.HIGH);

        when(serviceReqRepo.existsByVehicleIdAndStatusIn(anyString(), any())).thenReturn(false);
        when(serviceReqRepo.save(any(ServiceRequest.class))).thenReturn(mockRequest);
        when(userClient.getUser("u1")).thenReturn(new UserResponse());

        String res = srService.createRequest(dto);

        assertEquals("Service booked successfully", res);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void assignTechnician_Success() {
        AssignTechnicianDTO dto = new AssignTechnicianDTO();
        dto.setTechnicianId("tech1");
        dto.setBayNumber(1);

        UserResponse tech = new UserResponse();
        tech.setWorkload(0);
        tech.setMaxCapacity(5);

        ServiceBay bay = new ServiceBay();
        bay.setBayNumber(1);
        bay.setAvailable(true);

        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));
        when(userClient.getUser("tech1")).thenReturn(tech);
        when(bayService.findByBayNumber(1)).thenReturn(bay);
        when(serviceReqRepo.save(any(ServiceRequest.class))).thenReturn(mockRequest);

        String res = srService.assignTechnician("req1", dto);

        assertEquals("Assigned successfully", res);
        assertEquals(ServiceStatus.ASSIGNED, mockRequest.getStatus());
        verify(bayService).occupyBay(1);
    }

    @Test
    void startJob_Success() {
        mockRequest.setStatus(ServiceStatus.ASSIGNED);
        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));

        String res = srService.startJob("req1");

        assertEquals("Job started successfully", res);
        assertEquals(ServiceStatus.IN_PROGRESS, mockRequest.getStatus());
    }

    @Test
    void updateStatus_Complete_Success() {
        mockRequest.setStatus(ServiceStatus.IN_PROGRESS);
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(ServiceStatus.COMPLETED);

        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));

        String res = srService.updateStatus("req1", dto);

        assertEquals("Service marked as completed", res);
        assertEquals(ServiceStatus.COMPLETED, mockRequest.getStatus());
    }

    @Test
    void closeRequest_Success() {
        mockRequest.setStatus(ServiceStatus.COMPLETED);
        mockRequest.setCustomerId("c1");
        mockRequest.setBayNumber(1);
        mockRequest.setTechnicianId("t1");

        Invoice inv = new Invoice();
        inv.setId("inv1");
        inv.setTotal(100.0);

        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));
        when(billingService.generateInvoice("req1", 50.0)).thenReturn(inv);
        when(userClient.getUser("c1")).thenReturn(new UserResponse());

        String res = srService.closeRequest("req1", 50.0);

        assertTrue(res.contains("Service closed successfully"));
        assertEquals(ServiceStatus.CLOSED, mockRequest.getStatus());
        verify(bayService).releaseBay(1);
        verify(userClient).decrement("t1");
    }

    @Test
    void requestParts_Success() {
        // Arrange
        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));
        mockRequest.setStatus(ServiceStatus.IN_PROGRESS);

        // Act
        com.servicerequest.model.UsedPart p = new com.servicerequest.model.UsedPart();
        p.setPartId("p1");
        String result = srService.requestParts("req1", java.util.List.of(p));
        // Assert
        assertEquals("Parts requested successfully", result);
        verify(serviceReqRepo).save(mockRequest);
    }

    @Test
    void getTechnicianPerformance_Success() {
        mockRequest.setStatus(ServiceStatus.COMPLETED);
        mockRequest.setTechnicianName("John Doe");

        when(serviceReqRepo.findByStatus(ServiceStatus.COMPLETED))
                .thenReturn(java.util.List.of(mockRequest));

        java.util.List<java.util.Map<String, Object>> result = srService.getTechnicianPerformance();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).get("technicianName"));
        assertEquals(1L, result.get(0).get("completedJobs"));
    }

    @Test
    void getTechnicianWorkload_Success() {
        mockRequest.setStatus(ServiceStatus.IN_PROGRESS);
        mockRequest.setTechnicianId("tech1");

        when(serviceReqRepo.findByStatusIn(any())).thenReturn(java.util.List.of(mockRequest));

        java.util.Map<String, Long> workload = srService.getTechnicianWorkload();

        assertEquals(1L, workload.get("tech1"));
    }

    @Test
    void approveParts_Success() {
        mockRequest.setPartsStatus(com.servicerequest.enums.PartsStatus.PARTS_REQUESTED);
        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));

        String res = srService.approveParts("req1", "mgr1");

        assertEquals("Parts approved successfully", res);
        assertEquals(com.servicerequest.enums.PartsStatus.PARTS_APPROVED, mockRequest.getPartsStatus());
    }

    @Test
    void getTechnicianRequests_Success() {
        when(serviceReqRepo.findByTechnicianId("tech1")).thenReturn(java.util.List.of(mockRequest));

        java.util.List<ServiceRequest> res = srService.getTechnicianRequests("tech1");

        assertEquals(1, res.size());
    }

    @Test
    void getById_Success() {
        when(serviceReqRepo.findById("req1")).thenReturn(Optional.of(mockRequest));
        ServiceRequest result = srService.getById("req1");
        assertNotNull(result);
        assertEquals("req1", result.getId());
    }
}
