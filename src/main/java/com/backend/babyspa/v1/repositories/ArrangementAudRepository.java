package com.backend.babyspa.v1.repositories;

import com.backend.babyspa.v1.models.ArrangementAud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrangementAudRepository extends JpaRepository<ArrangementAud, Integer> {}
