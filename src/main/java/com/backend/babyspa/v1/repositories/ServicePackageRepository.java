package com.backend.babyspa.v1.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.ServicePackage;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Integer> {

    boolean existsByServicePackageNameAndIsDeleted(String servicePackageName, boolean isDeleted);

    boolean existsByServicePackageNameAndServicePackageIdNotAndIsDeleted(String servicePackageName, int servicePackageId, boolean isDeleted);

    @Query(value = "SELECT MAX(s.price) FROM service_package s WHERE s.is_deleted = :isDeleted", nativeQuery = true)
    Double findMaxPriceAndIsDeleted(@Param("isDeleted") boolean isDeleted);

    @Query(value = """
                SELECT * FROM service_package sp
                WHERE (:searchText IS NULL OR LOWER(sp.service_package_name) LIKE LOWER(CONCAT('%', :searchText, '%')))
                AND (sp.price BETWEEN COALESCE(:priceStart, 0) AND COALESCE(:priceEnd, 99999999) OR (:priceStart IS NULL AND :priceEnd IS NULL))
                AND sp.tenant_id = :tenantId
                AND sp.is_deleted = :isDeleted
                ORDER BY sp.service_package_id DESC
            """, nativeQuery = true)
    Page<ServicePackage> findAllServicePackageNative(@Param("searchText") String searchText,
                                                     @Param("priceStart") BigDecimal priceStart, @Param("priceEnd") BigDecimal priceEnd,
                                                     @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted, Pageable pageable);

    List<ServicePackage> findAllByTenantIdAndIsDeleted(String tenantId, boolean isDeleted);

}
