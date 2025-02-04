package com.backend.babyspa.v1.config;

import java.lang.reflect.Field;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;

@Aspect
@Component
public class TenantAspect {

	@Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..))")
	public void saveMethod() {
	}

	@Before("saveMethod() && args(entity, ..)")
	public void setTenantIdBeforeSave(Object entity) {
		// Proverite da li entitet ima tenantId polje
		if (entity != null && entity.getClass().isAnnotationPresent(Entity.class)) {
			try {

				Field tenantIdField = null;
				try {
					tenantIdField = entity.getClass().getDeclaredField("tenantId");
				} catch (NoSuchFieldException e) {

					return;
				}

				tenantIdField.setAccessible(true);
				if (tenantIdField.get(entity) == null) {
					tenantIdField.set(entity, TenantContext.getTenant());
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
