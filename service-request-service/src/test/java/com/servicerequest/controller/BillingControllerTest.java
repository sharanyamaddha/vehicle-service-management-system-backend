package com.servicerequest.controller;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.servicerequest.model.Invoice;
import com.servicerequest.service.BillingService;

@ExtendWith(MockitoExtension.class)
public class BillingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private BillingController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void generate_Success() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");

        when(billingService.generateInvoice(anyString(), anyDouble())).thenReturn(invoice);

        mockMvc.perform(post("/api/invoices/generate/req1?laborCost=100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("inv1"));
    }

    @Test
    void getByCustomer_Success() throws Exception {
        when(billingService.getInvoicesByCustomer("cust1")).thenReturn(List.of(new Invoice()));

        mockMvc.perform(get("/api/invoices/customer/cust1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void payInvoice_Success() throws Exception {
        doNothing().when(billingService).payInvoice("inv1");

        mockMvc.perform(patch("/api/invoices/inv1/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment successful"));
    }

    @Test
    void getInvoice_Success() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");

        when(billingService.getInvoiceById("inv1")).thenReturn(invoice);

        mockMvc.perform(get("/api/invoices/inv1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("inv1"));
    }

    @Test
    void getRevenueStats_Success() throws Exception {
        when(billingService.getRevenueStats()).thenReturn(Map.of("total", 1000.0));

        mockMvc.perform(get("/api/invoices/revenue-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1000.0));
    }
}
