package com.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String buildServiceBookedMail(Map<String, Object> data) {

        String template = """
        <div style="font-family:Segoe UI,Arial,sans-serif; max-width:650px; margin:auto; border:1px solid #ddd;">
          <div style="background:#0d6efd; color:white; padding:15px;">
            <h2>🚗 Vehicle Service Management System</h2>
          </div>

          <div style="padding:20px; color:#333;">
            <h3>Service Request Successfully Booked</h3>

            <p>Dear Customer,</p>

            <p>Your service request has been successfully created. Please find the details below:</p>

            <table style="width:100%; margin-top:15px; border-collapse:collapse;">
              <tr><td><b>Request Number</b></td><td>{{requestNumber}}</td></tr>
              <tr><td><b>Vehicle ID</b></td><td>{{vehicleId}}</td></tr>
              <tr><td><b>Issue Reported</b></td><td>{{issue}}</td></tr>
              <tr><td><b>Priority</b></td><td>{{priority}}</td></tr>
              <tr><td><b>Status</b></td><td>{{status}}</td></tr>
            </table>

            <p style="margin-top:20px;">Our team will assign a technician shortly.</p>

            <p>Regards,<br><b>Vehicle Service Team</b></p>
          </div>

          <div style="background:#f5f5f5; text-align:center; padding:10px; font-size:12px;">
            This is an automated email. Please do not reply.
          </div>
        </div>
        """;

        return template
                .replace("{{requestNumber}}", String.valueOf(data.get("requestNumber")))
                .replace("{{vehicleId}}", String.valueOf(data.get("vehicleId")))
                .replace("{{issue}}", String.valueOf(data.get("issue")))
                .replace("{{priority}}", String.valueOf(data.get("priority")))
                .replace("{{status}}", String.valueOf(data.get("status")));
    }

    public void sendMail(String to, String subject, String body) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("no-reply@vehicleservice.com");
            helper.setText(body, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
