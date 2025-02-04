package com.backend.babyspa.v1.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.models.PaymentType;
import com.backend.babyspa.v1.models.Role;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.models.StatusType;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.models.UserRole;
import com.backend.babyspa.v1.models.UserRoleKey;
import com.backend.babyspa.v1.repositories.DiscountRepository;
import com.backend.babyspa.v1.repositories.PaymentTypeRepository;
import com.backend.babyspa.v1.repositories.RoleRepository;
import com.backend.babyspa.v1.repositories.StatusRepository;
import com.backend.babyspa.v1.repositories.StatusTypeRepository;
import com.backend.babyspa.v1.repositories.UserRepository;
import com.backend.babyspa.v1.repositories.UserRoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	StatusRepository statusRepository;

	@Autowired
	StatusTypeRepository statusTypeRepository;

	@Autowired
	DiscountRepository discountRepository;

	@Autowired
	PaymentTypeRepository paymentTypeRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {

		if (statusTypeRepository.count() == 0) {
			statusTypeRepository.save(new StatusType("reservation"));
			statusTypeRepository.save(new StatusType("arrangement"));
		}

		if (statusRepository.count() == 0) {
			statusRepository.save(new Status("Termin otkazan", "term_canceled",
					statusTypeRepository.findByStatusTypeCode("reservation")));
			statusRepository.save(new Status("Iskorišten termin", "term_used",
					statusTypeRepository.findByStatusTypeCode("reservation")));
			statusRepository.save(new Status("Termin nije iskorišten", "term_not_used",
					statusTypeRepository.findByStatusTypeCode("reservation")));
			statusRepository.save(new Status("Rezervisan termin", "term_reserved",
					statusTypeRepository.findByStatusTypeCode("reservation")));
			statusRepository
					.save(new Status("Plaćen", "paid", statusTypeRepository.findByStatusTypeCode("arrangement")));
			statusRepository.save(
					new Status("Nije plaćen", "not_paid", statusTypeRepository.findByStatusTypeCode("arrangement")));
			statusRepository
					.save(new Status("Kreiran", "created", statusTypeRepository.findByStatusTypeCode("arrangement")));
		}

		if (discountRepository.count() == 0) {
			discountRepository.save(new Discount(BigDecimal.valueOf(20), false, "20KM", "sunshine"));
			discountRepository.save(new Discount(BigDecimal.valueOf(30), false, "30KM", "sunshine"));
			discountRepository.save(new Discount(BigDecimal.valueOf(50), false, "50KM", "sunshine"));
			discountRepository.save(new Discount(BigDecimal.valueOf(20), true, "20%", "sunshine"));
			discountRepository.save(new Discount(BigDecimal.valueOf(30), true, "30%", "sunshine"));
			discountRepository.save(new Discount(BigDecimal.valueOf(40), true, "40%", "sunshine"));
			discountRepository.save(new Discount(BigDecimal.valueOf(50), true, "50%", "sunshine"));
		}

		if (paymentTypeRepository.count() == 0) {
			paymentTypeRepository.save(new PaymentType("Gotovinski", "cash", "sunshine"));
			paymentTypeRepository.save(new PaymentType("Poklon bon", "gift", "sunshine"));
		}

		if (roleRepository.count() == 0) {
			roleRepository.save(new Role("ROLE_SUPER_ADMIN"));
			roleRepository.save(new Role("ROLE_ADMIN"));
			roleRepository.save(new Role("ROLE_RESERVATION_MAINTAINER"));
			roleRepository.save(new Role("ROLE_ARRANGEMENT_MAINTAINER"));
			roleRepository.save(new Role("ROLE_BABY_MAINTAINER"));
			roleRepository.save(new Role("ROLE_SERVICE_PACKAGE_MAINTAINER"));
			roleRepository.save(new Role("ROLE_REPORT_OVERVIEW"));
		}

		if (userRepository.count() == 0) {

			User user = new User("user@mail", "user@sunshine", passwordEncoder.encode("user123456"), "User", "Sunshine",
					"sunshine");
			userRepository.save(user);
			Role role = roleRepository.findByRoleName("ROLE_SUPER_ADMIN").orElseThrow(() -> new NotFoundException(
					"Ne može se pronaći uloga SUPER ADMIN prilikom kreiranja inicijalnog korisnika"));
			UserRole userRole = new UserRole();
			userRole.setRole(role);
			userRole.setUser(user);
			userRole.setUserRoleKey(new UserRoleKey(user.getUserId(), role.getRoleId()));
			userRoleRepository.save(userRole);

		}

	}

}
