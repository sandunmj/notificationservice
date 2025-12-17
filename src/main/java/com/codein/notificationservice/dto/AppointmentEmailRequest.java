package com.codein.notificationservice.dto;

public class AppointmentEmailRequest {
    private String toEmail;
    private String doctorName;
    private String patientName;
    private String sessionStartTime;
    private String sessionEndTime;
    private String scenario;
    private String newStartTime;
    private String newEndTime;
    
    // Getters and Setters
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getSessionStartTime() { return sessionStartTime; }
    public void setSessionStartTime(String sessionStartTime) { this.sessionStartTime = sessionStartTime; }
    
    public String getSessionEndTime() { return sessionEndTime; }
    public void setSessionEndTime(String sessionEndTime) { this.sessionEndTime = sessionEndTime; }
    
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    
    public String getNewStartTime() { return newStartTime; }
    public void setNewStartTime(String newStartTime) { this.newStartTime = newStartTime; }
    
    public String getNewEndTime() { return newEndTime; }
    public void setNewEndTime(String newEndTime) { this.newEndTime = newEndTime; }
}