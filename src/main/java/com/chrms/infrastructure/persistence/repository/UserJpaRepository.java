package com.chrms.infrastructure.persistence.repository;

import com.chrms.domain.enums.Role;
import com.chrms.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    List<UserJpaEntity> findByRole(Role role);
    boolean existsByEmail(String email);
}
