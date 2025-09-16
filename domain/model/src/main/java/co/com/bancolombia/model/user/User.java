package co.com.bancolombia.model.user;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import co.com.bancolombia.model.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "password")
public class User {
    private UUID id;
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
