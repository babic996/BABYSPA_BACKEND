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
public class UpdateGiftCardDto {

    @NotNull(message = "Morate unijeti ID poklon kartice")
    private int giftCardId;

    @NotNull(message = "Morate unijeti serijski broj")
    private String serialNumber;

    private LocalDateTime expirationDate;

    @NotNull(message = "Morate unijeti da li je poklon kartica iskoristena")
    private boolean used;
}
