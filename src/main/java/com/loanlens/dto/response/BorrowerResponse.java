package com.loanlens.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BorrowerResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String panNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String employmentType;
    private Double annualIncome;
    private LocalDateTime createdAt;
}