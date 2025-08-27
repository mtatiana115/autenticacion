package co.com.bancolombia.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserRecordResponse(
    String userId,
    String documentId,
    String name,
    String lastname,
    LocalDate birthDate,
    String address,
    String email,
    String phone,
    BigDecimal baseSalary
) {

}
