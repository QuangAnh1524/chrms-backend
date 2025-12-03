package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.ChatMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageJpaEntity, Long> {
    List<ChatMessageJpaEntity> findByAppointmentId(Long appointmentId);
    
    List<ChatMessageJpaEntity> findByAppointmentIdAndCreatedAtAfter(Long appointmentId, LocalDateTime after);
    
    @Query("SELECT m FROM ChatMessageJpaEntity m WHERE m.appointmentId = :appointmentId " +
           "AND m.isRead = false AND m.senderId != :userId")
    List<ChatMessageJpaEntity> findUnreadByAppointmentIdAndUserId(
        @Param("appointmentId") Long appointmentId,
        @Param("userId") Long userId
    );
    
    @Modifying
    @Query("UPDATE ChatMessageJpaEntity m SET m.isRead = true WHERE m.id = :messageId")
    void markAsRead(@Param("messageId") Long messageId);
}

