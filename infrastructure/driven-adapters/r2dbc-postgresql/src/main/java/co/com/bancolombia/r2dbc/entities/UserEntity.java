package co.com.bancolombia.r2dbc.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("user_id")
    private String userId;
    @Column("document_id")
    private String documentId;
    private String name;
    private String lastname;
    @Column("birth_date")
    private LocalDate birthDate;
    private String address;
    private String email;
    private String phone;
    @Column("base_salary")
    private BigDecimal baseSalary;
    // @Column("rol_id")
    // private String rolId;
}
