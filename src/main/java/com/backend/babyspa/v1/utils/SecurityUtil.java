package com.backend.babyspa.v1.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.backend.babyspa.v1.models.AuthUserDetails;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.services.UserService;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

  @Autowired private UserService userService;

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof AuthUserDetails) {
      String username = ((AuthUserDetails) principal).getUsername();
      return userService.findByUsername(username);
    }

    return null;
  }
}
