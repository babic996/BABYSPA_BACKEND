package com.backend.babyspa.v1.specifications;

import com.backend.babyspa.v1.models.ServicePackage;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ServicePackageSpecifications {

  public static Specification<ServicePackage> withSearchText(String searchText) {
    return (root, query, cb) -> {
      if (searchText == null || searchText.isEmpty()) {
        return cb.conjunction();
      }
      return cb.like(
          cb.lower(root.get("servicePackageName")), "%" + searchText.toLowerCase() + "%");
    };
  }

  public static Specification<ServicePackage> priceRange(
      BigDecimal startPrice, BigDecimal endPrice) {
    return (root, query, cb) -> {
      Predicate predicate = cb.conjunction();
      if (startPrice != null) {
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), startPrice));
      }
      if (endPrice != null) {
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), endPrice));
      }
      return predicate;
    };
  }

  public static Specification<ServicePackage> tenantAndNotDeleted(String tenantId) {
    return (root, query, cb) ->
        cb.and(cb.equal(root.get("tenantId"), tenantId), cb.isFalse(root.get("isDeleted")));
  }
}
