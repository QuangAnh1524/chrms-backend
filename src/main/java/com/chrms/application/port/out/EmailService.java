package com.chrms.application.port.out;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendAppointmentConfirmation(String to, String patientName, String doctorName, String dateTime);
    void sendRecordApprovalNotification(String to, String patientName, String recordId);
}
