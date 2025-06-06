package com.backend.babyspa.v1.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndUserIdNot(String username, int userId);

    boolean existsByEmailAndUserIdNot(String email, int userId);

    List<User> findByTenantId(String tenantId);
}
