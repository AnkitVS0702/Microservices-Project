package com.ankitshiksharthi.microserviceproject.user_service.repository;

import com.ankitshiksharthi.microserviceproject.user_service.model.ApprovalRequest;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalStatus;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    List<ApprovalRequest> findByStatus(ApprovalStatus status);
    List<ApprovalRequest> findByStatusAndType(ApprovalStatus status, ApprovalType type);
    List<ApprovalRequest> findByVendorId(Long vendorId);
}

