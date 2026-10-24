package com.rama.fest.ramafest.reposatory;

import com.rama.fest.ramafest.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
