package com.chrms.infrastructure.adapter.email;

import com.chrms.application.port.out.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }

    @Override
    public void sendAppointmentConfirmation(String to, String patientName, String doctorName, String dateTime) {
        String subject = "Xác nhận đặt lịch khám";
        String body = String.format(
                "Chào %s,\n\nBạn đã đặt lịch khám thành công với %s vào lúc %s.\n" +
                        "Vui lòng đến trước 10 phút và mang theo giấy tờ cần thiết.\n\n" +
                        "Trân trọng,\nCHRMS",
                patientName,
                doctorName,
                dateTime
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendRecordApprovalNotification(String to, String patientName, String recordId) {
        String subject = "Hồ sơ khám đã được duyệt";
        String body = String.format(
                "Chào %s,\n\nHồ sơ khám mã %s của bạn đã được bác sĩ duyệt.\n" +
                        "Bạn có thể đăng nhập để xem chi tiết và đơn thuốc.\n\n" +
                        "Trân trọng,\nCHRMS",
                patientName,
                recordId
        );
        sendEmail(to, subject, body);
    }
}

