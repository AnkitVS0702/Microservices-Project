package com.ankitshiksharthi.microserviceproject.user_service.model;

import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalStatus;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApprovalType type;

    private Long referenceId;
    private Long vendorId;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}

