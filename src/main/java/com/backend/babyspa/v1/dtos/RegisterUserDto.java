package com.backend.babyspa.v1.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

	@NotNull(message = "Morate unijeti email")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa email")
	private String email;

	@NotNull(message = "Morate unijeti password")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa password")
	@Size(min = 8, message = "Password mora imati najmanje 8 karaktera")
	private String password;

	@NotNull(message = "Morate unijeti ime")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa ime")
	private String firstName;

	@NotNull(message = "Morate unijeti prezime")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa prezime")
	private String lastName;
}
