package com.backend.babyspa.v1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	private static final String[] permitAllURLs = { "/user/login" };

	private static final String[] reportURLs = { "/service-package-daily-report/find-all",
			"/reservation-daily-report/find-all" };
	private static final String[] babyURLs = { "/baby/find-by-id", "/baby/find-all", "/baby/save", "/baby/update",
			"/baby/delete" };
	private static final String[] arrangementURLs = { "/arrangement/find-by-id", "/arrangement/find-all",
			"/arrangement/save", "/arrangement/update", "/arrangement/delete", "/arrangement/find-price",
			"/service-package/find-all-list", "/baby/find-all-list", "/status/find-all-status-type-code",
			"/discount/find-all", "/payment-type/find-all", "/reservation/exists-by-arrangement",
			"/reservation/find-by-arrangement-id" };
	private static final String[] servicePackageURLs = { "/service-package/find-by-id", "/service-package/find-all",
			"/service-package/save", "/service-package/update", "/service-package/delete" };
	private static final String[] reservationURLs = { "/reservation/find-by-id", "/reservation/find-all",
			"/reservation/save", "/reservation/update", "/reservation/delete", "/reservation/find-by-arrangement-id",
			"/reservation/canceled", "/status/find-all-status-type-code", "/arrangement/find-all-list" };
	private static final String[] userModifyingURLs = { "/user/register", "/user/assign-roles" };
	private static final String[] superAdminURLs = { "/user/add-new-tenant" };

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOrigin("http://37.27.245.73:3000");
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(authorize -> authorize.requestMatchers(permitAllURLs).permitAll()
						.requestMatchers(servicePackageURLs)
						.hasAnyRole("SERVICE_PACKAGE_MAINTAINER", "ADMIN", "SUPER_ADMIN")
						.requestMatchers(reservationURLs).hasAnyRole("RESERVATION_MAINTAINER", "ADMIN", "SUPER_ADMIN")
						.requestMatchers(arrangementURLs).hasAnyRole("ARRANGEMENT_MAINTAINER", "ADMIN", "SUPER_ADMIN")
						.requestMatchers(reportURLs).hasAnyRole("REPORT_OVERVIEW", "ADMIN", "SUPER_ADMIN")
						.requestMatchers(superAdminURLs).hasRole("SUPER_ADMIN").requestMatchers(userModifyingURLs)
						.hasAnyRole("SUPER_ADMIN", "ADMIN").requestMatchers(babyURLs)
						.hasAnyRole("BABY_MAINTAINER", "ADMIN", "SUPER_ADMIN").anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
