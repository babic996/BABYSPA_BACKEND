package com.backend.babyspa.v1.repositories;

import com.backend.babyspa.v1.models.ArrangementSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrangementSnapshotRepository extends JpaRepository<ArrangementSnapshot, Integer> {
}
