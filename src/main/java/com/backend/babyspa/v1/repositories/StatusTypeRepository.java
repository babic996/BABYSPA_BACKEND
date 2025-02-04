package com.backend.babyspa.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.StatusType;

@Repository
public interface StatusTypeRepository extends JpaRepository<StatusType, Integer> {

	StatusType findByStatusTypeCode(String statusTypeCode);
}
