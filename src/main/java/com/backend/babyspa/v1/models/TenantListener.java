package com.backend.babyspa.v1.models;

import com.backend.babyspa.v1.config.TenantContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

// sluzi kao backup za TenantAspect (U sustini, ako je TenantAspect ne radi, onda se koristi ovaj
// listener)
@Component
public class TenantListener {

  @PrePersist
  public void setTenantOnCreate(TenantEntity entity) {
    String tenantId = TenantContext.getTenant();
    if (tenantId != null && entity.getTenantId() == null) {
      entity.setTenantId(tenantId);
    }
  }

  @PreUpdate
  public void setTenantOnUpdate(TenantEntity entity) {
    String tenantId = TenantContext.getTenant();
    if (tenantId != null && entity.getTenantId() == null) {
      entity.setTenantId(tenantId);
    }
  }
}
