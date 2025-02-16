package com.backend.babyspa.v1.models;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUserDetails implements UserDetails {

	private String username;
	private String password;
	private boolean active;
	private List<GrantedAuthority> authorities;

	public AuthUserDetails() {

	}

	public AuthUserDetails(User user, List<UserRole> userRoles) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.active = true;
		this.authorities = userRoles.stream().map(e -> new SimpleGrantedAuthority(e.getRole().getRoleName()))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

}
