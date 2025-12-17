package com.codein.notificationservice.controller;

import com.codein.notificationservice.dto.ApiResponse;
import com.codein.notificationservice.dto.AppointmentEmailRequest;
import com.codein.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificationservice/api/email")
public class EmailController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;
    
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendEmail(@RequestParam String toEmail,
                                           @RequestParam String subject,
                                           @RequestParam String content) {
        try {
            logger.info("Received email request for: {} with subject: {}", toEmail, subject);
            emailService.sendEmail(toEmail, subject, content);
            logger.info("Email request processed successfully for: {}", toEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Email sent successfully"));
        } catch (Exception e) {
            logger.error("Email request failed for: {}, error: {}", toEmail, e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "Email sending failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/appointment")
    public ResponseEntity<ApiResponse> sendAppointmentEmail(
            @RequestParam String toEmail,
            @RequestParam String doctorName,
            @RequestParam String patientName,
            @RequestParam String sessionStartTime,
            @RequestParam String sessionEndTime,
            @RequestParam String scenario,
            @RequestParam(required = false) String newStartTime,
            @RequestParam(required = false) String newEndTime) {
        try {
            logger.info("Received appointment email request for: {} with scenario: {}", toEmail, scenario);
            emailService.sendAppointmentEmail(toEmail, doctorName, patientName, sessionStartTime, 
                    sessionEndTime, scenario, newStartTime, newEndTime);
            logger.info("Appointment email processed successfully for: {}", toEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Appointment email sent successfully"));
        } catch (Exception e) {
            logger.error("Appointment email failed for: {}, error: {}", toEmail, e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "Appointment email sending failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/appointment/json")
    public ResponseEntity<ApiResponse> sendAppointmentEmailJson(@RequestBody AppointmentEmailRequest request) {
        try {
            logger.info("Received JSON appointment email request for: {} with scenario: {}", 
                    request.getToEmail(), request.getScenario());
            emailService.sendAppointmentEmail(request.getToEmail(), request.getDoctorName(), 
                    request.getPatientName(), request.getSessionStartTime(), request.getSessionEndTime(), 
                    request.getScenario(), request.getNewStartTime(), request.getNewEndTime());
            logger.info("JSON appointment email processed successfully for: {}", request.getToEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Appointment email sent successfully"));
        } catch (Exception e) {
            logger.error("JSON appointment email failed for: {}, error: {}", 
                    request.getToEmail(), e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "Appointment email sending failed: " + e.getMessage()));
        }
    }
}