package com.rama.fest.ramafest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 512)
    private String path;

    @Column(length = 255)
    private String username;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(nullable = false)
    private Instant createdAt;


}
