package com.chrms.presentation.mapper;

import com.chrms.application.dto.command.BookAppointmentCommand;
import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.presentation.dto.request.BookAppointmentRequest;
import com.chrms.presentation.dto.response.AppointmentResponse;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public BookAppointmentCommand toCommand(BookAppointmentRequest request, Long patientId) {
        return BookAppointmentCommand.builder()
                .patientId(patientId)
                .doctorId(request.getDoctorId())
                .hospitalId(request.getHospitalId())
                .departmentId(request.getDepartmentId())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .build();
    }

    public AppointmentResponse toResponse(AppointmentResult result) {
        return AppointmentResponse.builder()
                .id(result.getId())
                .patientId(result.getPatientId())
                .patientName(result.getPatientName())
                .doctorId(result.getDoctorId())
                .doctorName(result.getDoctorName())
                .hospitalId(result.getHospitalId())
                .hospitalName(result.getHospitalName())
                .departmentId(result.getDepartmentId())
                .departmentName(result.getDepartmentName())
                .appointmentDate(result.getAppointmentDate())
                .appointmentTime(result.getAppointmentTime())
                .queueNumber(result.getQueueNumber())
                .status(result.getStatus())
                .notes(result.getNotes())
                .createdAt(result.getCreatedAt())
                .build();
    }
}