package com.backend.babyspa.v1.repositories;

import java.util.List;

import com.backend.babyspa.v1.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.models.UserRole;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    @Transactional
    @Modifying
    public void deleteByUser(User user);

    public List<UserRole> findByUser(User user);


}
