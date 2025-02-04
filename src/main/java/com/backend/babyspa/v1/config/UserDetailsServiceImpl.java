package com.backend.babyspa.v1.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.models.AuthUserDetails;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.models.UserRole;
import com.backend.babyspa.v1.repositories.UserRepository;
import com.backend.babyspa.v1.repositories.UserRoleRepository;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Nije pronađen korisnik sa username-om:" + username));
		List<UserRole> userRoles = userRoleRepository.findByUser(user);

		AuthUserDetails auth = new AuthUserDetails(user, userRoles);
		return auth;
	}

}
