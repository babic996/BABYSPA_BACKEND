package com.backend.babyspa.v1.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.models.ServicePackage;

@Repository
public interface ArrangementRepository extends JpaRepository<Arrangement, Integer> {

	@Query(value = "SELECT * FROM arrangement ORDER BY arrangement_id DESC", nativeQuery = true)
	List<Arrangement> findAllNative();

	List<Arrangement> findByRemainingTermGreaterThan(int remainingTerm);

	boolean existsByServicePackage(ServicePackage servicePackage);

	boolean existsByBaby(Baby baby);

	@Query(value = """
			SELECT a.*
			FROM arrangement a
			JOIN status s ON a.status_id = s.status_id
			JOIN baby b ON a.baby_id = b.baby_id
			LEFT JOIN payment_type pt ON a.payment_type_id = pt.payment_type_id
			JOIN service_package sp ON a.service_package_id = sp.service_package_id
			WHERE (:statusId IS NULL OR s.status_id = :statusId)
			AND (:babyId IS NULL OR b.baby_id = :babyId)
			AND (:arrangementId IS NULL OR a.arrangement_id = :arrangementId)
			AND (:paymentTypeId IS NULL OR pt.payment_type_id = :paymentTypeId)
			AND (:servicePackageId IS NULL OR sp.service_package_id = :servicePackageId)
			AND (:startPrice IS NULL OR :endPrice IS NULL OR a.price BETWEEN :startPrice AND :endPrice)
			AND (:remainingTerm IS NULL OR a.remaining_term = :remainingTerm)
			AND (a.tenant_id = :tenantId)
			ORDER BY a.arrangement_id DESC
			""", nativeQuery = true)
	List<Arrangement> findAllArrangementNative(@Param("statusId") Integer statusId, @Param("babyId") Integer babyId,
			@Param("paymentTypeId") Integer paymentTypeId, @Param("startPrice") BigDecimal startPrice,
			@Param("endPrice") BigDecimal endPrice, @Param("remainingTerm") Integer remainingTerm,
			@Param("servicePackageId") Integer servicePackageId, @Param("arrangementId") Integer arrangementId,
			@Param("tenantId") String tenantId);

	@Query(value = """
			SELECT a.*
			FROM arrangement a
			JOIN status s ON a.status_id = s.status_id
			JOIN baby b ON a.baby_id = b.baby_id
			LEFT JOIN payment_type pt ON a.payment_type_id = pt.payment_type_id
			JOIN service_package sp ON a.service_package_id = sp.service_package_id
			WHERE (:statusId IS NULL OR s.status_id = :statusId)
			AND (:babyId IS NULL OR b.baby_id = :babyId)
			AND (:arrangementId IS NULL OR a.arrangement_id = :arrangementId)
			AND (:paymentTypeId IS NULL OR pt.payment_type_id = :paymentTypeId)
			AND (:servicePackageId IS NULL OR sp.service_package_id = :servicePackageId)
			AND (:startPrice IS NULL OR :endPrice IS NULL OR a.price BETWEEN :startPrice AND :endPrice)
			AND (:remainingTerm IS NULL OR a.remaining_term = :remainingTerm)
			AND (a.created_at >= :startDate AND a.created_at <= :endDate)
			AND (a.tenant_id = :tenantId)
			ORDER BY a.arrangement_id DESC
			""", nativeQuery = true)
	List<Arrangement> findAllArrangementNativeWithStartDateAndDate(@Param("statusId") Integer statusId,
			@Param("babyId") Integer babyId, @Param("paymentTypeId") Integer paymentTypeId,
			@Param("startPrice") BigDecimal startPrice, @Param("endPrice") BigDecimal endPrice,
			@Param("remainingTerm") Integer remainingTerm, @Param("servicePackageId") Integer servicePackageId,
			@Param("arrangementId") Integer arrangementId, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("tenantId") String tenantId);

	@Query(value = """
			SELECT SUM(a.price)
			FROM arrangement a
			JOIN status s ON a.status_id = s.status_id
			JOIN baby b ON a.baby_id = b.baby_id
			LEFT JOIN payment_type pt ON a.payment_type_id = pt.payment_type_id
			JOIN service_package sp ON a.service_package_id = sp.service_package_id
			WHERE (:statusId IS NULL OR s.status_id = :statusId)
			AND (:babyId IS NULL OR b.baby_id = :babyId)
			AND (:arrangementId IS NULL OR a.arrangement_id = :arrangementId)
			AND (:paymentTypeId IS NULL OR pt.payment_type_id = :paymentTypeId)
			AND (:servicePackageId IS NULL OR sp.service_package_id = :servicePackageId)
			AND (:startPrice IS NULL OR :endPrice IS NULL OR a.price BETWEEN :startPrice AND :endPrice)
			AND (:remainingTerm IS NULL OR a.remaining_term = :remainingTerm)
			AND (a.tenant_id = :tenantId)
			""", nativeQuery = true)
	BigDecimal findPriceForAllArrangementNative(@Param("statusId") Integer statusId, @Param("babyId") Integer babyId,
			@Param("paymentTypeId") Integer paymentTypeId, @Param("startPrice") BigDecimal startPrice,
			@Param("endPrice") BigDecimal endPrice, @Param("remainingTerm") Integer remainingTerm,
			@Param("servicePackageId") Integer servicePackageId, @Param("arrangementId") Integer arrangementId,
			@Param("tenantId") String tenantId);

	@Query(value = """
			SELECT SUM(a.price)
			FROM arrangement a
			JOIN status s ON a.status_id = s.status_id
			JOIN baby b ON a.baby_id = b.baby_id
			LEFT JOIN payment_type pt ON a.payment_type_id = pt.payment_type_id
			JOIN service_package sp ON a.service_package_id = sp.service_package_id
			WHERE (:statusId IS NULL OR s.status_id = :statusId)
			AND (:babyId IS NULL OR b.baby_id = :babyId)
			AND (:arrangementId IS NULL OR a.arrangement_id = :arrangementId)
			AND (:paymentTypeId IS NULL OR pt.payment_type_id = :paymentTypeId)
			AND (:servicePackageId IS NULL OR sp.service_package_id = :servicePackageId)
			AND (:startPrice IS NULL OR :endPrice IS NULL OR a.price BETWEEN :startPrice AND :endPrice)
			AND (:remainingTerm IS NULL OR a.remaining_term = :remainingTerm)
			AND (a.created_at >= :startDate AND a.created_at <= :endDate)
			AND (a.tenant_id = :tenantId)
			""", nativeQuery = true)
	BigDecimal findPriceForAllArrangementNativeWithStartDateAndDate(@Param("statusId") Integer statusId,
			@Param("babyId") Integer babyId, @Param("paymentTypeId") Integer paymentTypeId,
			@Param("startPrice") BigDecimal startPrice, @Param("endPrice") BigDecimal endPrice,
			@Param("remainingTerm") Integer remainingTerm, @Param("servicePackageId") Integer servicePackageId,
			@Param("arrangementId") Integer arrangementId, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("tenantId") String tenantId);
}
