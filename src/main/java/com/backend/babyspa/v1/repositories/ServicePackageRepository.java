package com.backend.babyspa.v1.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.ServicePackage;

@Repository
public interface ServicePackageRepository
    extends JpaRepository<ServicePackage, Integer>, JpaSpecificationExecutor<ServicePackage> {

  boolean existsByServicePackageNameAndIsDeleted(String servicePackageName, boolean isDeleted);

  boolean existsByServicePackageNameAndServicePackageIdNotAndIsDeleted(
      String servicePackageName, int servicePackageId, boolean isDeleted);

  @Query(
      value = "SELECT MAX(s.price) FROM service_package s WHERE s.is_deleted = :isDeleted",
      nativeQuery = true)
  Double findMaxPriceAndIsDeleted(@Param("isDeleted") boolean isDeleted);

  List<ServicePackage> findAllByIsDeleted(boolean isDeleted);
}
