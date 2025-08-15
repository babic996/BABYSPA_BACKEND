package com.backend.babyspa.v1.repositories;

import com.backend.babyspa.v1.models.ReservationHistoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationHistoryStatusRepository extends JpaRepository<ReservationHistoryStatus, Integer> {
}
