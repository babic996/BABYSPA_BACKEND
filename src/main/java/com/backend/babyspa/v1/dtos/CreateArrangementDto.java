package com.backend.babyspa.v1.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateArrangementDto {

  @Min(value = 1, message = "Morate izabrati validan popust")
  private Integer discountId;

  private String note;

  @NotNull(message = "Morate bebu za koju pravi aranzman")
  @Min(value = 1, message = "Morate izabrati validnu bebu")
  private int babyId;

  @NotNull(message = "Morate izabrati paket usluge")
  @Min(value = 1, message = "Morate izabrati validan paket usluge")
  private Integer servicePackageId;

  @Min(value = 1, message = "Morate izabrati validnu poklon karticu")
  private Integer giftCardId;
}
