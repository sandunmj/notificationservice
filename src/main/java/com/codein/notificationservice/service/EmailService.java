package com.codein.notificationservice.service;

import com.codein.notificationservice.entity.EmailRecord;
import com.codein.notificationservice.repository.EmailRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final EmailRecordRepository emailRecordRepository;
    private final SesClient sesClient;
    private final String fromEmail;
    
    public EmailService(EmailRecordRepository emailRecordRepository,
                       @Value("${aws.region}") String region,
                       @Value("${aws.accessKeyId}") String accessKeyId,
                       @Value("${aws.secretKey}") String secretKey,
                       @Value("${aws.ses.fromEmail}") String fromEmail) {
        this.emailRecordRepository = emailRecordRepository;
        this.fromEmail = fromEmail;
        
        logger.info("Initializing SES client with region: {}", region);
        logger.info("From email: {}", fromEmail);
        logger.info("Access Key ID: {}***", accessKeyId.substring(0, Math.min(4, accessKeyId.length())));
        
        this.sesClient = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretKey)))
                .build();
        
        logger.info("SES client initialized successfully");
    }
    
    public void sendEmail(String toEmail, String subject, String content) {
        try {
            logger.info("=== EMAIL SEND REQUEST START ===");
            logger.info("From: {}", fromEmail);
            logger.info("To: {}", toEmail);
            logger.info("Subject: {}", subject);
            logger.info("Content: {}", content);
            
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(Destination.builder().toAddresses(toEmail).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).build())
                            .body(Body.builder()
                                    .text(Content.builder().data(content).build())
                                    .build())
                            .build())
                    .build();
            
            logger.info("Calling AWS SES...");
            SendEmailResponse response = sesClient.sendEmail(request);
            
            logger.info("=== AWS SES RESPONSE ===");
            logger.info("MessageId: {}", response.messageId());
            logger.info("HTTP Status: {}", response.sdkHttpResponse().statusCode());
            logger.info("Response Headers: {}", response.sdkHttpResponse().headers());
            
            EmailRecord record = new EmailRecord(toEmail, subject, content);
            emailRecordRepository.save(record);
            logger.info("✓ Email record saved to database");
            logger.info("=== EMAIL SEND REQUEST END ===");
            
        } catch (Exception e) {
            logger.error("=== EMAIL SEND FAILED ===");
            logger.error("Error Type: {}", e.getClass().getSimpleName());
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Full Stack Trace:", e);
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
    
    public void sendAppointmentEmail(String toEmail, String doctorName, String patientName, 
                                   String sessionStartTime, String sessionEndTime, String scenario,
                                   String newStartTime, String newEndTime) {
        String subject = getSubjectByScenario(scenario);
        String content = formatEmailContent(doctorName, patientName, sessionStartTime, 
                sessionEndTime, scenario, newStartTime, newEndTime);
        sendEmail(toEmail, subject, content);
    }
    
    private String getSubjectByScenario(String scenario) {
        switch (scenario.toLowerCase()) {
            case "confirmed":
                return "✅ Appointment Confirmed - Hospital Management System";
            case "start":
                return "🏥 Your Appointment Has Started";
            case "stop":
                return "✅ Appointment Completed";
            case "reschedule":
                return "📅 Appointment Rescheduled";
            default:
                return "📋 Appointment Update";
        }
    }
    
    private String formatEmailContent(String doctorName, String patientName, String sessionStartTime,
                                    String sessionEndTime, String scenario, String newStartTime, String newEndTime) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear ").append(patientName).append(",\n\n");
        
        switch (scenario.toLowerCase()) {
            case "confirmed":
                content.append("Your appointment has been CONFIRMED with the following details:\n\n")
                       .append("👨‍⚕️ Doctor: ").append(doctorName).append("\n")
                       .append("📅 Date & Time: ").append(sessionStartTime).append(" - ").append(sessionEndTime).append("\n\n")
                       .append("Please arrive 15 minutes before your scheduled time.\n")
                       .append("Bring your ID and any relevant medical documents.");
                break;
                
            case "start":
                content.append("Your appointment with Dr. ").append(doctorName).append(" has STARTED.\n\n")
                       .append("📍 Please proceed to the consultation room.\n")
                       .append("⏰ Session Time: ").append(sessionStartTime).append(" - ").append(sessionEndTime);
                break;
                
            case "stop":
                content.append("Your appointment with Dr. ").append(doctorName).append(" has been COMPLETED.\n\n")
                       .append("⏰ Session Duration: ").append(sessionStartTime).append(" - ").append(sessionEndTime).append("\n\n")
                       .append("Thank you for visiting our hospital.\n")
                       .append("Please collect your prescription and follow-up instructions from the reception.");
                break;
                
            case "reschedule":
                content.append("Your appointment with Dr. ").append(doctorName).append(" has been RESCHEDULED.\n\n")
                       .append("❌ Previous Time: ").append(sessionStartTime).append(" - ").append(sessionEndTime).append("\n")
                       .append("✅ New Time: ").append(newStartTime).append(" - ").append(newEndTime).append("\n\n")
                       .append("Please make note of the new timing and arrive 15 minutes early.");
                break;
                
            default:
                content.append("Your appointment details:\n\n")
                       .append("Doctor: ").append(doctorName).append("\n")
                       .append("Time: ").append(sessionStartTime).append(" - ").append(sessionEndTime);
        }
        
        content.append("\n\n---\n")
               .append("Hospital Management System\n")
               .append("For any queries, please contact: +60-123-456-789\n")
               .append("Email: support@hospital.com");
        
        return content.toString();
    }
}