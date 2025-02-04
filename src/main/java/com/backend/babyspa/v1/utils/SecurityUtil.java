package com.backend.babyspa.v1.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.backend.babyspa.v1.models.AuthUserDetails;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.services.UserService;

public class SecurityUtil {

	public static User getCurrentUser(UserService userService) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof AuthUserDetails) {
			AuthUserDetails authUserDetails = (AuthUserDetails) principal;
			return userService.findByUsername(authUserDetails.getUsername());

		}

		return null;
	}
}
