package co.com.bancolombia.api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema
public record CreateUserRecord(
        @NotBlank(message = "Document ID cannot be empty")
        String documentId,

        @NotBlank(message = "First name cannot be empty")
        String name,

        @NotBlank(message = "Last name cannot be empty")
        String lastname,

        LocalDate birthDate,

        String address,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        String phone,

        @NotNull(message = "Base salary cannot be empty")
        @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
        @DecimalMax(value = "15000001", inclusive = false, message = "Salary must be less than or equal to 15000000")
        BigDecimal baseSalary
) {}
