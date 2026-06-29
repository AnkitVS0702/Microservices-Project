package com.ankitshiksharthi.microserviceproject.user_service.service;

import com.ankitshiksharthi.microserviceproject.user_service.dto.ApprovalRequestResponse;
import com.ankitshiksharthi.microserviceproject.user_service.model.ApprovalRequest;
import com.ankitshiksharthi.microserviceproject.user_service.model.VendorProfile;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalStatus;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalType;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.VendorStatus;
import com.ankitshiksharthi.microserviceproject.user_service.repository.ApprovalRequestRepository;
import com.ankitshiksharthi.microserviceproject.user_service.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {
    private final ApprovalRequestRepository approvalRequestRepository;
    private final VendorProfileRepository vendorProfileRepository;

    @Transactional(readOnly = true)
    public List<ApprovalRequestResponse> getPendingApprovals() {
        log.info("Fetching all pending approvals");
        List<ApprovalRequest> requests = approvalRequestRepository.findByStatus(ApprovalStatus.PENDING);
        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequestResponse> getPendingApprovalsByType(ApprovalType type) {
        log.info("Fetching pending approvals for type: {}", type);
        List<ApprovalRequest> requests = approvalRequestRepository.findByStatusAndType(ApprovalStatus.PENDING, type);
        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApprovalRequestResponse approveRequest(Long requestId, String adminNotes) {
        log.info("Approving request with id: {}", requestId);

        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Approval request not found with id: " + requestId));

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Request is not in pending status");
        }

        request.setStatus(ApprovalStatus.APPROVED);
        request.setAdminNotes(adminNotes);
        request.setResolvedAt(LocalDateTime.now());

        // If this is a vendor registration approval, update vendor profile status
        if (request.getType() == ApprovalType.VENDOR_REGISTRATION) {
            VendorProfile vendorProfile = vendorProfileRepository.findById(request.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("Vendor profile not found"));
            vendorProfile.setStatus(VendorStatus.ACTIVE);
            vendorProfileRepository.save(vendorProfile);
            log.info("Vendor profile status updated to ACTIVE");
        }

        ApprovalRequest savedRequest = approvalRequestRepository.save(request);
        return mapToResponse(savedRequest);
    }

    public ApprovalRequestResponse rejectRequest(Long requestId, String adminNotes) {
        log.info("Rejecting request with id: {}", requestId);

        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Approval request not found with id: " + requestId));

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Request is not in pending status");
        }

        request.setStatus(ApprovalStatus.REJECTED);
        request.setAdminNotes(adminNotes);
        request.setResolvedAt(LocalDateTime.now());

        // If this is a vendor registration rejection, update vendor profile status
        if (request.getType() == ApprovalType.VENDOR_REGISTRATION) {
            VendorProfile vendorProfile = vendorProfileRepository.findById(request.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("Vendor profile not found"));
            vendorProfile.setStatus(VendorStatus.REJECTED);
            vendorProfileRepository.save(vendorProfile);
            log.info("Vendor profile status updated to REJECTED");
        }

        ApprovalRequest savedRequest = approvalRequestRepository.save(request);
        return mapToResponse(savedRequest);
    }

    private ApprovalRequestResponse mapToResponse(ApprovalRequest request) {
        return ApprovalRequestResponse.builder()
                .id(request.getId())
                .type(request.getType())
                .referenceId(request.getReferenceId())
                .vendorId(request.getVendorId())
                .status(request.getStatus())
                .adminNotes(request.getAdminNotes())
                .createdAt(request.getCreatedAt())
                .resolvedAt(request.getResolvedAt())
                .build();
    }
}

