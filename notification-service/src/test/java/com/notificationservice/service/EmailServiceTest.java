package com.notificationservice.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void buildServiceBookedMail_Success() {
        Map<String, Object> data = Map.of(
                "requestNumber", "SR123",
                "vehicleId", "V1",
                "issue", "Brake Fail",
                "priority", "HIGH",
                "status", "REQUESTED");

        String html = emailService.buildServiceBookedMail(data);

        assertTrue(html.contains("SR123"));
        assertTrue(html.contains("Brake Fail"));
    }

    @Test
    void buildInviteMail_Success() {
        Map<String, Object> data = Map.of(
                "username", "john_doe",
                "role", "TECHNICIAN",
                "link", "http://test.com/activate?token=abc");

        String html = emailService.buildInviteMail(data);

        assertTrue(html.contains("john_doe"));
        assertTrue(html.contains("TECHNICIAN"));
        assertTrue(html.contains("token=abc"));
    }

    @Test
    void buildServiceAssignedMail_Success() {
        Map<String, Object> data = Map.of(
                "requestNumber", "SR123",
                "vehicleId", "V1",
                "technicianName", "Alex",
                "specialization", "MECHANIC",
                "bayNumber", "2");

        String html = emailService.buildServiceAssignedMail(data);

        assertTrue(html.contains("Alex"));
        assertTrue(html.contains("Bay Number"));
        assertTrue(html.contains("2"));
    }

    @Test
    void buildInvoiceGenerated_Success() {
        Map<String, Object> data = Map.of(
                "invoiceId", "INV-001",
                "amount", "150.0",
                "status", "PENDING");

        String html = emailService.buildInvoiceGeneratedMail(data);

        assertTrue(html.contains("INV-001"));
        assertTrue(html.contains("$150.0"));
    }

    @Test
    void sendMail_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // We can't easily mock MimeMessageHelper's internal setting of mimeMessage
        // fields heavily without PowerMock
        // But we can verify mailSender.send() is called

        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendMail("test@example.com", "Subject", "Body");

        verify(mailSender).send(mimeMessage);
    }
}
