package com.backend.babyspa.v1.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
                            @NonNull HttpServletResponse response, 
                            @NonNull Object handler) {
        // Prvo provjeri da li je tenant već postavljen (npr. iz JWT filtera)
        String tenantId = TenantContext.getTenant();
        
        // Ako nije postavljen, pokušaj da ga izvučeš iz header-a
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = request.getHeader("X-Tenant-ID");
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setTenant(tenantId);
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, 
                                @NonNull HttpServletResponse response, 
                                @NonNull Object handler, 
                                @org.springframework.lang.Nullable Exception ex) {
        // Obriši tenant kontekst na kraju request-a
        TenantContext.clear();
    }
}

