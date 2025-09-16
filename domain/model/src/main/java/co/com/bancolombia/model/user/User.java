package co.com.bancolombia.model.user;
import java.math.BigDecimal;
import java.time.LocalDate;

import co.com.bancolombia.model.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String id;
    private String documentId;
    private String name;
    private String lastname;
    private LocalDate birthDate;
    private String address;
    private String email;
    private String phone;
    private BigDecimal baseSalary;
    private Rol rol;
    private String password;
}
