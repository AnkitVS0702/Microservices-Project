package com.ankitshiksharthi.microserviceproject.user_service.controller;

import com.ankitshiksharthi.microserviceproject.user_service.dto.VendorApplyRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.VendorProfileResponse;
import com.ankitshiksharthi.microserviceproject.user_service.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/vendor")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @PostMapping("/apply")
    public ResponseEntity<VendorProfileResponse> applyAsVendor(
            @RequestParam Long userId,
            @Valid @RequestBody VendorApplyRequest request) {
        VendorProfileResponse response = vendorService.applyAsVendor(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<VendorProfileResponse> getVendorProfile(@PathVariable Long userId) {
        VendorProfileResponse response = vendorService.getVendorProfile(userId);
        return ResponseEntity.ok(response);
    }
}

