package com.backend.babyspa.v1.specifications;

import com.backend.babyspa.v1.models.Baby;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BabySpecifications {
  public static Specification<Baby> withSearchText(String searchText) {
    return (root, query, cb) -> {
      if (searchText == null || searchText.isEmpty()) {
        return cb.conjunction();
      }
      String cleanSearch = searchText.replace("+", "");
      return cb.or(
          cb.like(cb.lower(root.get("babyName")), "%" + searchText.toLowerCase() + "%"),
          cb.like(cb.lower(root.get("babySurname")), "%" + searchText.toLowerCase() + "%"),
          cb.like(
              cb.function(
                  "regexp_replace",
                  String.class,
                  root.get("phoneNumber"),
                  cb.literal("\\+"),
                  cb.literal("g")),
              "%" + cleanSearch + "%"));
    };
  }

  public static Specification<Baby> dateRange(LocalDateTime start, LocalDateTime end) {
    return (root, query, cb) -> {
      Predicate predicate = cb.conjunction();
      if (start != null) {
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("birthDate"), start));
      }
      if (end != null) {
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("birthDate"), end));
      }
      return predicate;
    };
  }

  public static Specification<Baby> tenantAndNotDeleted(String tenantId) {
    return (root, query, cb) ->
        cb.and(cb.equal(root.get("tenantId"), tenantId), cb.isFalse(root.get("isDeleted")));
  }
}
