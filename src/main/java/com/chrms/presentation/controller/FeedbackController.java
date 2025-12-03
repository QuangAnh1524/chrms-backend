package com.chrms.presentation.controller;

import com.chrms.application.usecase.patient.SubmitFeedbackUseCase;
import com.chrms.domain.entity.Feedback;
import com.chrms.domain.repository.FeedbackRepository;
import com.chrms.presentation.dto.request.SubmitFeedbackRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.FeedbackResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Feedback APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class FeedbackController {

    private final SubmitFeedbackUseCase submitFeedbackUseCase;
    private final FeedbackRepository feedbackRepository;
    private final com.chrms.infrastructure.cache.RedisCacheService cacheService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit feedback", description = "Submit feedback for an appointment")
    public ApiResponse<FeedbackResponse> submitFeedback(
            @Valid @RequestBody SubmitFeedbackRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        Feedback feedback = submitFeedbackUseCase.execute(
                request.getAppointmentId(),
                userId,
                request.getRating(),
                request.getComment()
        );
        
        FeedbackResponse response = FeedbackResponse.builder()
                .id(feedback.getId())
                .appointmentId(feedback.getAppointmentId())
                .patientId(feedback.getPatientId())
                .doctorId(feedback.getDoctorId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
        
        return ApiResponse.success("Feedback submitted successfully", response);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get feedback by doctor", description = "Get all feedback for a doctor")
    public ApiResponse<List<FeedbackResponse>> getFeedbackByDoctor(@PathVariable Long doctorId) {
        List<Feedback> feedbacks = feedbackRepository.findByDoctorId(doctorId);
        
        List<FeedbackResponse> response = feedbacks.stream()
                .map(feedback -> FeedbackResponse.builder()
                        .id(feedback.getId())
                        .appointmentId(feedback.getAppointmentId())
                        .patientId(feedback.getPatientId())
                        .doctorId(feedback.getDoctorId())
                        .rating(feedback.getRating())
                        .comment(feedback.getComment())
                        .createdAt(feedback.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ApiResponse.success(response);
    }

    @GetMapping("/doctor/{doctorId}/average-rating")
    @Operation(summary = "Get average rating", description = "Get average rating for a doctor (cached)")
    public ApiResponse<Double> getAverageRating(@PathVariable Long doctorId) {
        // Check cache first
        String cacheKey = com.chrms.infrastructure.cache.RedisCacheService.CACHE_DOCTOR_RATING_PREFIX + doctorId;
        Object cached = cacheService.get(cacheKey);
        if (cached != null) {
            return ApiResponse.success((Double) cached);
        }
        
        // Get from DB and cache
        Double averageRating = feedbackRepository.getAverageRatingByDoctorId(doctorId);
        double rating = averageRating != null ? averageRating : 0.0;
        cacheService.set(cacheKey, rating, 10, java.util.concurrent.TimeUnit.MINUTES);
        
        return ApiResponse.success(rating);
    }
}

