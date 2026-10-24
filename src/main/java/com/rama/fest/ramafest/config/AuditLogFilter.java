package com.rama.fest.ramafest.config;

import com.rama.fest.ramafest.entity.AuditLog;
import com.rama.fest.ramafest.reposatory.AuditLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuditLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogFilter.class);

    private final AuditLogRepository auditLogRepository;
    private final ClientIpResolver clientIpResolver;

    public AuditLogFilter(AuditLogRepository auditLogRepository, ClientIpResolver clientIpResolver) {
        this.auditLogRepository = auditLogRepository;
        this.clientIpResolver = clientIpResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {
            saveAuditLog(request, response);
        }
    }

    private void saveAuditLog(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : null;

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod(request.getMethod());
        auditLog.setPath(request.getRequestURI());
        auditLog.setUsername(username);
        auditLog.setIpAddress(clientIpResolver.resolve(request));
        auditLog.setStatusCode(response.getStatus());
        auditLog.setCreatedAt(Instant.now());

        try {
            auditLogRepository.save(auditLog);
        } catch (DataAccessException exception) {
            logger.error("Unable to persist audit log for {} {}", request.getMethod(), request.getRequestURI(), exception);
        }
    }
}
