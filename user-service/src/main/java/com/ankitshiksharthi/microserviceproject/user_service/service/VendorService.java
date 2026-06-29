package com.ankitshiksharthi.microserviceproject.user_service.service;

import com.ankitshiksharthi.microserviceproject.user_service.dto.VendorApplyRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.VendorProfileResponse;
import com.ankitshiksharthi.microserviceproject.user_service.model.ApprovalRequest;
import com.ankitshiksharthi.microserviceproject.user_service.model.User;
import com.ankitshiksharthi.microserviceproject.user_service.model.VendorProfile;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalStatus;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalType;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.UserRole;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.VendorStatus;
import com.ankitshiksharthi.microserviceproject.user_service.repository.ApprovalRequestRepository;
import com.ankitshiksharthi.microserviceproject.user_service.repository.UserRepository;
import com.ankitshiksharthi.microserviceproject.user_service.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VendorService {
    private final VendorProfileRepository vendorProfileRepository;
    private final UserRepository userRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public VendorProfileResponse applyAsVendor(Long userId, VendorApplyRequest request) {
        log.info("Processing vendor application for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if user already has a vendor profile
        if (vendorProfileRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already has a vendor profile");
        }

        // Create vendor profile
        VendorProfile vendorProfile = VendorProfile.builder()
                .userId(userId)
                .businessName(request.getBusinessName())
                .businessDescription(request.getBusinessDescription())
                .gstNumber(request.getGstNumber())
                .status(VendorStatus.PENDING_APPROVAL)
                .build();

        VendorProfile savedProfile = vendorProfileRepository.save(vendorProfile);
        log.info("Vendor profile created with id: {} (pending approval)", savedProfile.getId());

        // Create approval request
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .type(ApprovalType.VENDOR_REGISTRATION)
                .referenceId(savedProfile.getId())
                .vendorId(userId)
                .status(ApprovalStatus.PENDING)
                .build();

        approvalRequestRepository.save(approvalRequest);
        log.info("Approval request created for vendor registration");

        return mapToResponse(savedProfile);
    }

    @Transactional(readOnly = true)
    public VendorProfileResponse getVendorProfile(Long userId) {
        log.info("Fetching vendor profile for userId: {}", userId);
        VendorProfile profile = vendorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor profile not found for userId: " + userId));
        return mapToResponse(profile);
    }

    private VendorProfileResponse mapToResponse(VendorProfile profile) {
        return VendorProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .businessName(profile.getBusinessName())
                .businessDescription(profile.getBusinessDescription())
                .gstNumber(profile.getGstNumber())
                .status(profile.getStatus())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}

