package co.com.bancolombia.jwtadapter;

import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements IPasswordEncoder {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<String> encode(String password) {
        return Mono.just(passwordEncoder.encode(password));
    }

    @Override
    public Mono<Boolean> matches(String password, String hash) {
        return Mono.just(passwordEncoder.matches(password,hash));
    }
}
