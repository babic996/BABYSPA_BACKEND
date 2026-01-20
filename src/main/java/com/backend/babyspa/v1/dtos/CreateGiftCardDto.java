package com.backend.babyspa.v1.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGiftCardDto {

  @NotNull(message = "Morate unijeti serijski broj")
  private String serialNumber;

  private LocalDateTime expirationDate;
}
