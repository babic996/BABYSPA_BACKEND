package com.backend.babyspa.v1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.repositories.UserRoleRepository;

@Service
public class UserRoleService {

	@Autowired
	UserRoleRepository userRoleRepository;

}
