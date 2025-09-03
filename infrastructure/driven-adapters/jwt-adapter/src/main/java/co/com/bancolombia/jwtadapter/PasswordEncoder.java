package co.com.bancolombia.jwtadapter;

import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PasswordEncoder implements IPasswordEncoder {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Mono<String> encode(String password) {
        return Mono.just(bCryptPasswordEncoder.encode(password));
    }

    @Override
    public Mono<Boolean> matches(String password, String hash) {
        return Mono.just(bCryptPasswordEncoder.matches(password,hash));
    }
}
