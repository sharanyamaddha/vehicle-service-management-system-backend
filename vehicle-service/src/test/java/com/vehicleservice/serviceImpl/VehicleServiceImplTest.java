package com.vehicleservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vehicleservice.model.Vehicle;
import com.vehicleservice.repository.VehicleRepository;
import com.vehicleservice.requestdto.VehicleRequest;
import com.vehicleservice.responsedto.VehicleResponse;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepo;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle mockVehicle;

    @BeforeEach
    void setUp() {
        mockVehicle = new Vehicle();
        mockVehicle.setId("1");
        mockVehicle.setOwnerId("user1");
        mockVehicle.setRegistrationNumber("AP01AB1234");
        mockVehicle.setMake("Toyota");
        mockVehicle.setModel("Camry");
        mockVehicle.setYear(2020);
        mockVehicle.setColor("White");
        // mockVehicle.setType("CAR");
        mockVehicle.setDescription("Test Desc");
    }

    @Test
    void addVehicle_Success() {
        VehicleRequest req = new VehicleRequest();
        req.setOwnerId("user1");
        req.setRegistrationNumber("AP01AB1234");
        req.setMake("Toyota");
        req.setModel("Camry");
        req.setYear(2020);

        when(vehicleRepo.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(vehicleRepo.save(any(Vehicle.class))).thenReturn(mockVehicle);

        String res = vehicleService.addVehicle(req);

        assertEquals("Vehicle added succesfully", res);
        verify(vehicleRepo).save(any(Vehicle.class));
    }

    @Test
    void addVehicle_AlreadyExists() {
        VehicleRequest req = new VehicleRequest();
        req.setRegistrationNumber("AP01AB1234");

        when(vehicleRepo.existsByRegistrationNumber("AP01AB1234")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> vehicleService.addVehicle(req));
    }

    @Test
    void getVehiclesByCustomer_Success() {
        when(vehicleRepo.findByOwnerId("user1")).thenReturn(List.of(mockVehicle));

        List<VehicleResponse> list = vehicleService.getVehiclesByCustomer("user1");

        assertEquals(1, list.size());
        assertEquals("Toyota", list.get(0).getMake());
    }

    @Test
    void getVehicleById_Success() {
        when(vehicleRepo.findById("1")).thenReturn(Optional.of(mockVehicle));

        VehicleResponse res = vehicleService.getVehicleById("1");

        assertEquals("AP01AB1234", res.getRegistrationNumber());
    }

    @Test
    void getVehicleById_NotFound() {
        when(vehicleRepo.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehicleService.getVehicleById("invalid"));
    }

    @Test
    void updateVehicle_Success() {
        VehicleRequest req = new VehicleRequest();
        req.setMake("Honda");
        req.setModel("Civic");

        when(vehicleRepo.findById("1")).thenReturn(Optional.of(mockVehicle));

        vehicleService.updateVehicle("1", req);

        assertEquals("Honda", mockVehicle.getMake());
        assertEquals("Civic", mockVehicle.getModel());
        verify(vehicleRepo).save(mockVehicle);
    }

    @Test
    void deleteVehicle_Success() {
        doNothing().when(vehicleRepo).deleteById("1");

        String res = vehicleService.deleteVehicle("1");

        assertEquals("Vehicle deleted", res);
        verify(vehicleRepo).deleteById("1");
    }

    @Test
    void getAllVehicles_Success() {
        when(vehicleRepo.findAll()).thenReturn(List.of(mockVehicle));

        List<VehicleResponse> list = vehicleService.getAllVehicles();

        assertEquals(1, list.size());
        assertEquals("AP01AB1234", list.get(0).getRegistrationNumber());
    }

    @Test
    void existsByRegistrationNumber_Success() {
        when(vehicleRepo.existsByRegistrationNumber("AP01AB1234")).thenReturn(true);
        boolean exists = vehicleService.existsByRegistrationNumber("AP01AB1234");
        assertTrue(exists);
    }

    @Test
    void updateVehicle_NotFound() {
        VehicleRequest req = new VehicleRequest();
        when(vehicleRepo.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehicleService.updateVehicle("invalid", req));
    }
}
