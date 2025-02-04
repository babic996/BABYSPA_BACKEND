package com.backend.babyspa.v1.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {

	boolean existsByValueAndIsPrecentage(BigDecimal value, boolean isPrecentage);

	boolean existsByValueAndIsPrecentageAndDiscountIdNot(BigDecimal value, boolean isPrecentage, int discountId);

	List<Discount> findByTenantId(String tenantId);

}
