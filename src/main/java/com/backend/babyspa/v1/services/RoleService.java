package com.backend.babyspa.v1.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Role;
import com.backend.babyspa.v1.repositories.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository roleRepository;

	public Role findById(int roleId) throws NotFoundException {

		return roleRepository.findById(roleId)
				.orElseThrow(() -> new NotFoundException("Nije pronađena uloga sa ID: " + roleId + "!"));
	}

	public Role findByRoleName(String roleName) throws NotFoundException {

		return roleRepository.findByRoleName(roleName)
				.orElseThrow(() -> new NotFoundException("Nije pronađena uloga sa nazivom: " + roleName + "!"));
	}

	public List<Role> findAll() {
		return roleRepository.findAll();
	}

}
