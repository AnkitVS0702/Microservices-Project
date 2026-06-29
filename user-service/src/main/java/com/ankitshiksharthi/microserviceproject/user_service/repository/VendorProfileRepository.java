package com.ankitshiksharthi.microserviceproject.user_service.repository;

import com.ankitshiksharthi.microserviceproject.user_service.model.VendorProfile;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {
    Optional<VendorProfile> findByUserId(Long userId);
    long countByStatus(VendorStatus status);
}

