package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.auth.gateways.IAuthProvider;
import co.com.bancolombia.model.user.gateways.UserRepository;

import reactor.core.publisher.Mono;

import java.util.Objects;

public class AuthUseCase {

    private final UserRepository userRepository;
    private  final IAuthProvider authProvider;

    public AuthUseCase(UserRepository userRepository, IAuthProvider authProvider) {
        this.userRepository = userRepository;
        this.authProvider = authProvider;
    }

    public Mono<Auth> signIn (String email, String password){
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("credenciales invalidas")))
                .flatMap(user -> {
                    if (!Objects.equals(password, user.getPassword())){
                        return Mono.error(new IllegalArgumentException("credenciales invalidas"));
                    }
                    return authProvider.generateToken(user);
                });
    }
}
