package co.com.bancolombia.model.auth;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

public record Auth(
        String token
) {
}
