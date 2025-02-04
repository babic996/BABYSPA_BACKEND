package com.backend.babyspa.v1.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.models.StatusType;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

	boolean existsByStatusNameAndStatusCodeAndStatusType(String statusName, String statusCode, StatusType statusType);

	boolean existsByStatusNameAndStatusCodeAndStatusTypeAndStatusIdNot(String statusName, String statusCode,
			StatusType statusType, int statusId);

	Optional<Status> findByStatusCode(String statusCode);

	List<Status> findByStatusType_StatusTypeCode(String statusTypeCode);

}
