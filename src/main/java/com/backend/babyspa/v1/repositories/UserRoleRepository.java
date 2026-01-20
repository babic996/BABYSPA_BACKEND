package com.backend.babyspa.v1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.models.UserRole;
import org.springframework.transaction.annotation.Transactional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

  @Transactional
  @Modifying
  void deleteByUser(User user);

  List<UserRole> findByUser(User user);
}
