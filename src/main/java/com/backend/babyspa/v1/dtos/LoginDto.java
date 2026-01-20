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
public class LoginDto {

  @NotNull(message = "Morate unijeti username")
  @NotBlank(message = "Poslali ste samo razmake")
  @Pattern(regexp = "^(?! ).*$", message = "Unijeli ste prvo razmak pa username")
  private String username;

  @NotNull(message = "Morate unijeti password")
  @NotBlank(message = "Poslali ste samo razmake")
  @Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa password")
  @Size(min = 8, message = "Password mora imati najmanje 8 karaktera")
  private String password;
}
