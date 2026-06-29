package com.ankitshiksharthi.microserviceproject.user_service.model;

import com.ankitshiksharthi.microserviceproject.user_service.model.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String businessName;
    private String businessDescription;
    private String gstNumber;

    @Enumerated(EnumType.STRING)
    private VendorStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

