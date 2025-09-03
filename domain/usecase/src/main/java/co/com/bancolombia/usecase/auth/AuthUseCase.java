package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.auth.gateways.IAuthProvider;
import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.gateways.UserRepository;

import co.com.bancolombia.usecase.rol.RolUseCase;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class AuthUseCase {

    private final UserRepository userRepository;
    private  final IAuthProvider authProvider;
    private final RolUseCase rolUseCase;
    private final IPasswordEncoder passwordEncoder;

    public AuthUseCase(UserRepository userRepository, IAuthProvider authProvider, RolUseCase rolUseCase, IPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authProvider = authProvider;
        this.rolUseCase = rolUseCase;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Auth> signIn (String email, String password){
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("credenciales invalidas")))
                .flatMap(user -> rolUseCase.getById(user.getRol().getId())
                        .flatMap(rol -> {
                            user.setRol(rol);
                        return Mono.just(user);
                        })
                    )
                .flatMap(user ->
                        passwordEncoder.matches(password, user.getPassword())
                                .flatMap(match -> {
                                    if (!match.equals(Boolean.TRUE)){
                                        return Mono.error(new IllegalArgumentException("credenciales invalidas"));
                                    }
                                    return authProvider.generateToken(user);
                                })
                        );
    }
}
