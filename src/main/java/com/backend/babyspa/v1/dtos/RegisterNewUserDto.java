package com.backend.babyspa.v1.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterNewUserDto extends RegisterUserDto {

	@NotNull(message = "Morate unijeti username")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*$", message = "Unijeli ste prvo razmak pa username")
	@Pattern(regexp = "^[^@]*$", message = "Username ne smije sadržavati znak '@'")
	private String username;
}
