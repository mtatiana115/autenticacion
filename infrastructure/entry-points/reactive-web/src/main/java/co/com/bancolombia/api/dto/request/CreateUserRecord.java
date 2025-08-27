package co.com.bancolombia.api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRecord(
    @NotBlank(message = "El documento de identidad no puede estar vacío")
    String documentId,

    @NotBlank(message = "El nombre no puede estar vacío")
    String name,

    @NotBlank(message = "El apellido no puede estar vacío")
    String lastname,

    LocalDate birthDate,

    String address,

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico no tiene un formato válido")
    String email,

    String phone,

    @NotNull(message = "El salario base no puede estar vacío")
    @DecimalMin(value = "0.0", inclusive = false, message = "El salario debe ser mayor a cero")
    @DecimalMax(value = "15000001", inclusive = false, message = "El salario debe ser menor o giual a 15000000")
    BigDecimal baseSalary
) {}
