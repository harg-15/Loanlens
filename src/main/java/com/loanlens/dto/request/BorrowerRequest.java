package com.loanlens.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BorrowerRequest {
    @NotBlank
    private String fullName;
    @Email @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @NotBlank
    private String panNumber;
    @NotNull
    private LocalDate dateOfBirth;
    private String address;
    @NotBlank
    private String employmentType;
    @PositiveOrZero
    private Double annualIncome;
}