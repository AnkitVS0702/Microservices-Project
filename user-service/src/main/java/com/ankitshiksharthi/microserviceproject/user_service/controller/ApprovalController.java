package com.ankitshiksharthi.microserviceproject.user_service.controller;

import com.ankitshiksharthi.microserviceproject.user_service.dto.ApprovalRequestResponse;
import com.ankitshiksharthi.microserviceproject.user_service.dto.ApprovalRequestUpdateRequest;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalType;
import com.ankitshiksharthi.microserviceproject.user_service.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/approvals")
@RequiredArgsConstructor
public class ApprovalController {
    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<List<ApprovalRequestResponse>> getPendingApprovals(
            @RequestParam(required = false) ApprovalType type) {
        List<ApprovalRequestResponse> response;
        if (type != null) {
            response = approvalService.getPendingApprovalsByType(type);
        } else {
            response = approvalService.getPendingApprovals();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<ApprovalRequestResponse> approveRequest(
            @PathVariable Long requestId,
            @RequestBody ApprovalRequestUpdateRequest request) {
        ApprovalRequestResponse response = approvalService.approveRequest(requestId, request.getAdminNotes());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApprovalRequestResponse> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody ApprovalRequestUpdateRequest request) {
        ApprovalRequestResponse response = approvalService.rejectRequest(requestId, request.getAdminNotes());
        return ResponseEntity.ok(response);
    }
}

