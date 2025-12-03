package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.DoctorScheduleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleJpaRepository extends JpaRepository<DoctorScheduleJpaEntity, Long> {
    List<DoctorScheduleJpaEntity> findByDoctorId(Long doctorId);
    List<DoctorScheduleJpaEntity> findByDoctorIdAndDayOfWeek(Long doctorId, Integer dayOfWeek);
    List<DoctorScheduleJpaEntity> findByDoctorIdAndIsAvailable(Long doctorId, Boolean isAvailable);
    
    @Query("SELECT s FROM DoctorScheduleJpaEntity s WHERE s.doctorId = :doctorId " +
           "AND s.dayOfWeek = :dayOfWeek AND s.isAvailable = true " +
           "AND s.startTime <= :time AND s.endTime > :time")
    Optional<DoctorScheduleJpaEntity> findAvailableSchedule(
        @Param("doctorId") Long doctorId,
        @Param("dayOfWeek") Integer dayOfWeek,
        @Param("time") LocalTime time
    );
}

