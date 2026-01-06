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

  public String buildInviteMail(Map<String, Object> data) {
    String template = """
        <div style="font-family:Segoe UI,Arial,sans-serif; max-width:650px; margin:auto; border:1px solid #ddd;">
          <div style="background:#0d6efd; color:white; padding:15px;">
            <h2>Vehicle Service Management System</h2>
          </div>

          <div style="padding:20px; color:#333;">
            <h3>You've been invited!</h3>

            <p>Hello {{username}},</p>

            <p>You have been invited to join the Vehicle Service Management System as a <b>{{role}}</b>.</p>

            <p>Please click the button below to activate your account and set your password:</p>

            <div style="text-align:center; margin:30px 0;">
            	<a href="{{link}}" style="background:#0d6efd; color:white; padding:12px 24px; text-decoration:none; border-radius:4px; font-weight:bold;">Activate Account</a>
            </div>

            <p>This link will expire in 48 hours.</p>

            <p>Regards,<br><b>Admin Team</b></p>
          </div>
        </div>
        """;
    return template
        .replace("{{username}}", String.valueOf(data.get("username")))
        .replace("{{role}}", String.valueOf(data.get("role")))
        .replace("{{link}}", String.valueOf(data.get("link")));
  }

  public String buildServiceAssignedMail(Map<String, Object> data) {
    String template = """
        <div style="font-family:Segoe UI,Arial,sans-serif; max-width:650px; margin:auto; border:1px solid #ddd;">
          <div style="background:#0d6efd; color:white; padding:15px;">
            <h2>Vehicle Service Management System</h2>
          </div>
          <div style="padding:20px; color:#333;">
            <h3>Technician Assigned</h3>
            <p>Dear Customer,</p>
            <p>Your service request <b>{{requestNumber}}</b> for vehicle <b>{{vehicleId}}</b> has been assigned to a technician.</p>
            <table style="width:100%; margin-top:15px; border-collapse:collapse;">
              <tr><td><b>Technician Name</b></td><td>{{technicianName}}</td></tr>
              <tr><td><b>Specialization</b></td><td>{{specialization}}</td></tr>
              <tr><td><b>Bay Number</b></td><td>{{bayNumber}}</td></tr>
              <tr><td><b>Status</b></td><td>IN PROGRESS</td></tr>
            </table>
            <p style="margin-top:20px;">Work will begin shortly.</p>
            <p>Regards,<br><b>Vehicle Service Team</b></p>
          </div>
        </div>
        """;
    return template
        .replace("{{requestNumber}}", String.valueOf(data.get("requestNumber")))
        .replace("{{vehicleId}}", String.valueOf(data.get("vehicleId")))
        .replace("{{technicianName}}", String.valueOf(data.get("technicianName")))
        .replace("{{specialization}}", String.valueOf(data.get("specialization")))
        .replace("{{bayNumber}}", String.valueOf(data.get("bayNumber")));
  }

  public String buildServiceClosedMail(Map<String, Object> data) {
    String template = """
        <div style="font-family:Segoe UI,Arial,sans-serif; max-width:650px; margin:auto; border:1px solid #ddd;">
          <div style="background:#198754; color:white; padding:15px;">
            <h2>Vehicle Service Management System</h2>
          </div>
          <div style="padding:20px; color:#333;">
            <h3>Service Completed & Closed</h3>
            <p>Dear Customer,</p>
            <p>Your service request <b>{{requestNumber}}</b> for vehicle <b>{{vehicleId}}</b> has been successfully completed and closed.</p>
            <p>Please check your invoice for payment details.</p>
            <p>Regards,<br><b>Vehicle Service Team</b></p>
          </div>
        </div>
        """;
    return template
        .replace("{{requestNumber}}", String.valueOf(data.get("requestNumber")))
        .replace("{{vehicleId}}", String.valueOf(data.get("vehicleId")));
  }

  public String buildInvoiceGeneratedMail(Map<String, Object> data) {
    String template = """
        <div style="font-family:Segoe UI,Arial,sans-serif; max-width:650px; margin:auto; border:1px solid #ddd;">
          <div style="background:#ffc107; color:black; padding:15px;">
            <h2>Vehicle Service Management System</h2>
          </div>
          <div style="padding:20px; color:#333;">
            <h3>Invoice Generated</h3>
            <p>Dear Customer,</p>
            <p>An invoice has been generated for your recent service.</p>
            <table style="width:100%; margin-top:15px; border-collapse:collapse;">
              <tr><td><b>Invoice ID</b></td><td>{{invoiceId}}</td></tr>
              <tr><td><b>Amount Due</b></td><td>${{amount}}</td></tr>
              <tr><td><b>Status</b></td><td>{{status}}</td></tr>
            </table>
            <p style="margin-top:20px;">Please login to your dashboard to view and pay the invoice.</p>
            <p>Regards,<br><b>Vehicle Service Team</b></p>
          </div>
        </div>
        """;
    return template
        .replace("{{invoiceId}}", String.valueOf(data.get("invoiceId")))
        .replace("{{amount}}", String.valueOf(data.get("amount")))
        .replace("{{status}}", String.valueOf(data.get("status")));
  }

  public void sendMail(String to, String subject, String body) {

    try {
      System.out.println("Attempting to send email to: " + to);
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setFrom("no-reply@vehicleservice.com");
      helper.setText(body, true);

      mailSender.send(message);
      System.out.println("Email sent successfully to: " + to);

    } catch (MessagingException e) {
      System.err.println("Failed to send email to " + to + ": " + e.getMessage());
      throw new RuntimeException("Email sending failed", e);
    }
  }
}
