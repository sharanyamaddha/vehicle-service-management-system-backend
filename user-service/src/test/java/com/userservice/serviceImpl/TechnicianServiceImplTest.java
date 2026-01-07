package com.userservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.userservice.model.Technician;
import com.userservice.model.enums.Specialization;
import com.userservice.repository.TechnicianRepository;
import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;

@ExtendWith(MockitoExtension.class)
public class TechnicianServiceImplTest {

    @Mock
    private TechnicianRepository techRepository;

    @InjectMocks
    private TechnicianServiceImpl techService;

    private Technician mockTech;

    @BeforeEach
    void setUp() {
        mockTech = new Technician();
        mockTech.setId("tech1");
        mockTech.setUserId("user1");
        mockTech.setSpecialization(Specialization.BODYWORK);
        mockTech.setAvailable(true);
        mockTech.setCurrentJobs(2);
    }

    @Test
    void createTechnician_Success() {
        TechnicianCreateRequest req = new TechnicianCreateRequest();
        req.setUserId("user1");
        req.setSpecialization(Specialization.BODYWORK);

        when(techRepository.save(any(Technician.class))).thenReturn(mockTech);

        TechnicianResponse res = techService.createTechnician(req);

        assertNotNull(res);
        assertEquals("user1", res.getUserId());
        assertEquals(Specialization.BODYWORK, res.getSpecialization());
    }

    @Test
    void getTechniciansByStatus_Success() {
        when(techRepository.findByAvailable(true)).thenReturn(List.of(mockTech));

        List<TechnicianResponse> list = techService.getTechniciansByStatus(true);

        assertEquals(1, list.size());
        assertTrue(list.get(0).isAvailable());
    }

    @Test
    void incrementWorkload_Success() {
        when(techRepository.findById("tech1")).thenReturn(Optional.of(mockTech));

        techService.incrementWorkload("tech1");

        assertEquals(3, mockTech.getCurrentJobs());
        verify(techRepository).save(mockTech);
    }

    @Test
    void decrementWorkload_Success() {
        when(techRepository.findById("tech1")).thenReturn(Optional.of(mockTech));

        techService.decrementWorkload("tech1");

        assertEquals(1, mockTech.getCurrentJobs());
        verify(techRepository).save(mockTech);
    }

    @Test
    void decrementWorkload_NotBelowZero() {
        mockTech.setCurrentJobs(0);
        when(techRepository.findById("tech1")).thenReturn(Optional.of(mockTech));

        techService.decrementWorkload("tech1");

        assertEquals(0, mockTech.getCurrentJobs());
        verify(techRepository).save(mockTech);
    }

    @Test
    void getAvailableBySpecialization_Success() {
        when(techRepository.findBySpecializationAndAvailable("BODYWORK", true)).thenReturn(List.of(mockTech));

        List<TechnicianResponse> list = techService.getAvailableBySpecialization("BODYWORK");

        assertEquals(1, list.size());
        assertEquals(Specialization.BODYWORK, list.get(0).getSpecialization());
    }

    @Test
    void incrementWorkload_FindByUserId() {
        when(techRepository.findById("tech1")).thenReturn(Optional.empty());
        when(techRepository.findByUserId("tech1")).thenReturn(Optional.of(mockTech));

        techService.incrementWorkload("tech1");

        assertEquals(3, mockTech.getCurrentJobs());
        verify(techRepository).save(mockTech);
    }

    @Test
    void incrementWorkload_NotFound() {
        when(techRepository.findById("invalid")).thenReturn(Optional.empty());
        when(techRepository.findByUserId("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> techService.incrementWorkload("invalid"));
    }

    @Test
    void decrementWorkload_FindByUserId() {
        when(techRepository.findById("tech1")).thenReturn(Optional.empty());
        when(techRepository.findByUserId("tech1")).thenReturn(Optional.of(mockTech));

        techService.decrementWorkload("tech1");

        assertEquals(1, mockTech.getCurrentJobs());
        verify(techRepository).save(mockTech);
    }

    @Test
    void decrementWorkload_NotFound() {
        when(techRepository.findById("invalid")).thenReturn(Optional.empty());
        when(techRepository.findByUserId("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> techService.decrementWorkload("invalid"));
    }
}
