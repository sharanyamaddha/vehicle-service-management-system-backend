package com.servicerequest.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.servicerequest.model.ServiceBay;
import com.servicerequest.repository.ServiceBayRepository;

@ExtendWith(MockitoExtension.class)
public class ServiceBayServiceImplTest {

    @Mock
    private ServiceBayRepository bayRepo;

    @InjectMocks
    private ServiceBayServiceImpl bayService;

    private ServiceBay mockBay;

    @BeforeEach
    void setUp() {
        mockBay = new ServiceBay();
        mockBay.setId("bay1");
        mockBay.setBayNumber(1);
        mockBay.setAvailable(true);
        mockBay.setActive(true);
    }

    @Test
    void createBay_Success() {
        when(bayRepo.findByBayNumber(1)).thenReturn(Optional.empty());
        when(bayRepo.save(any(ServiceBay.class))).thenReturn(mockBay);

        ServiceBay res = bayService.createBay(mockBay);

        assertNotNull(res);
        assertEquals(1, res.getBayNumber());
    }

    @Test
    void createBay_AlreadyExists() {
        when(bayRepo.findByBayNumber(1)).thenReturn(Optional.of(mockBay));

        assertThrows(ResponseStatusException.class, () -> bayService.createBay(mockBay));
    }

    @Test
    void occupyBay_Success() {
        when(bayRepo.findByBayNumber(1)).thenReturn(Optional.of(mockBay));

        bayService.occupyBay(1);

        assertFalse(mockBay.isAvailable());
        verify(bayRepo).save(mockBay);
    }

    @Test
    void occupyBay_AlreadyOccupied() {
        mockBay.setAvailable(false);
        when(bayRepo.findByBayNumber(1)).thenReturn(Optional.of(mockBay));

        assertThrows(RuntimeException.class, () -> bayService.occupyBay(1));
    }

    @Test
    void releaseBay_Success() {
        mockBay.setAvailable(false);
        when(bayRepo.findByBayNumber(1)).thenReturn(Optional.of(mockBay));

        bayService.releaseBay(1);

        assertTrue(mockBay.isAvailable());
        verify(bayRepo).save(mockBay);
    }

    @Test
    void getAvailableBays() {
        when(bayRepo.findByAvailableTrueAndActiveTrue()).thenReturn(List.of(mockBay));

        List<ServiceBay> list = bayService.getAvailableBays();

        assertEquals(1, list.size());
    }
}
