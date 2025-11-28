package com.backend.babyspa.v1.config;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class TenantAspect {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..))")
	public void saveMethod() {
	}

	@Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
	public void repositoryMethods() {
	}

	@Before("saveMethod() && args(entity, ..)")
	public void setTenantIdBeforeSave(Object entity) {
		// Provjerite da li entitet ima tenantId polje
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

	@Around("repositoryMethods()")
	public Object enableTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
		String tenantId = TenantContext.getTenant();
		if (tenantId != null && !tenantId.isBlank()) {
			try {
				// Pokušaj da dobiješ EntityManager iz trenutne transakcije
				EntityManager em = null;
				if (TransactionSynchronizationManager.isActualTransactionActive()) {
					em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
				}
				
				// Fallback na @PersistenceContext EntityManager
				if (em == null) {
					em = entityManager;
				}
				
				if (em != null) {
					Session session = em.unwrap(Session.class);
					if (session != null && session.isOpen()) {
						Filter filter = session.getEnabledFilter("tenantFilter");
						if (filter == null) {
							filter = session.enableFilter("tenantFilter");
							filter.setParameter("tenantId", tenantId);
						} else {
							// Ažuriraj parametar ako je već omogućen
							filter.setParameter("tenantId", tenantId);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return joinPoint.proceed();
	}
}
