package com.backend.babyspa.v1.services;

import com.backend.babyspa.v1.dtos.AssignRolesDto;
import com.backend.babyspa.v1.exceptions.BuisnessException;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Role;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.models.UserRole;
import com.backend.babyspa.v1.models.UserRoleKey;
import com.backend.babyspa.v1.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.repositories.UserRoleRepository;

import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    UserRepository userRepository;


    @Transactional
    public String assignRolesToUser(AssignRolesDto assignRolesDto, Authentication authentication) {
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!hasPermission) {
            throw new BuisnessException("Ovaj korisnik nema ovlaštenje da dodjeluje uloge drugim korisnicima.");
        }

        User user = userRepository.findById(assignRolesDto.getUserId())
                .orElseThrow(() -> new NotFoundException("Nije pronađen korisnik sa ID: " + assignRolesDto.getUserId() + "!"));

        userRoleRepository.deleteByUser(user);
        assignRolesDto.getRoleIds().forEach(roleId -> {
            UserRole userRole = new UserRole();
            Role role = roleService.findById(roleId);
            userRole.setRole(role);
            userRole.setUser(user);
            userRole.setUserRoleKey(new UserRoleKey(user.getUserId(), role.getRoleId()));
            userRoleRepository.save(userRole);
        });

        return "Uspješno ste dodijelili uloge korisniku: " + user.getUsername() + "!";
    }

    public List<Role> findByUser(User user) {
        return userRoleRepository.findByUser(user).stream().map(UserRole::getRole).toList();
    }


}
