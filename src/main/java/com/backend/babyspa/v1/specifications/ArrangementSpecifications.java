package com.backend.babyspa.v1.specifications;

import com.backend.babyspa.v1.models.Arrangement;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ArrangementSpecifications {

  public static Specification<Arrangement> withFilters(
      Integer statusId,
      Integer babyId,
      Integer paymentTypeId,
      Integer giftCardId,
      Integer servicePackageId,
      Integer arrangementId,
      Integer remainingTerm,
      BigDecimal startPrice,
      BigDecimal endPrice) {
    return (root, query, cb) -> {
      Predicate predicate = cb.conjunction();

      if (statusId != null)
        predicate = cb.and(predicate, cb.equal(root.join("status").get("statusId"), statusId));
      if (babyId != null)
        predicate = cb.and(predicate, cb.equal(root.join("baby").get("babyId"), babyId));
      if (paymentTypeId != null)
        predicate =
            cb.and(
                predicate, cb.equal(root.join("paymentType").get("paymentTypeId"), paymentTypeId));
      if (giftCardId != null)
        predicate =
            cb.and(predicate, cb.equal(root.join("giftCard").get("giftCardId"), giftCardId));
      if (servicePackageId != null)
        predicate =
            cb.and(
                predicate,
                cb.equal(root.join("servicePackage").get("servicePackageId"), servicePackageId));
      if (arrangementId != null)
        predicate = cb.and(predicate, cb.equal(root.get("arrangementId"), arrangementId));
      if (remainingTerm != null)
        predicate = cb.and(predicate, cb.equal(root.get("remainingTerm"), remainingTerm));
      if (startPrice != null && endPrice != null)
        predicate = cb.and(predicate, cb.between(root.get("price"), startPrice, endPrice));

      return predicate;
    };
  }

  public static Specification<Arrangement> dateRange(
      LocalDateTime startDate, LocalDateTime endDate) {
    return (root, query, cb) -> {
      Predicate predicate = cb.conjunction();
      if (startDate != null)
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
      if (endDate != null)
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
      return predicate;
    };
  }

  public static Specification<Arrangement> tenantAndNotDeleted(String tenantId) {
    return (root, query, cb) ->
        cb.and(cb.equal(root.get("tenantId"), tenantId), cb.isFalse(root.get("isDeleted")));
  }
}
