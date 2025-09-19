package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.auth.gateways.IAuthProvider;
import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserAuthRepository;

import co.com.bancolombia.usecase.rol.RolUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class AuthUseCase {

    private final UserUseCase userUseCase;
    private final IAuthProvider authProvider;
    private final RolUseCase rolUseCase;
    private final IPasswordEncoder passwordEncoder;
    private final UserAuthRepository userAuthRepository;

    public Mono<Auth> signIn (String email, String password){
        return userUseCase.findByEmail(email)
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

    public Mono<User> validateToken (String token){
        return authProvider.validateToken(token)
                .flatMap(isValid -> {
                    return  authProvider.getSubject(token)
                            .flatMap(userUseCase::findByEmail)
                            .flatMap(user -> rolUseCase
                                    .getById(user.getRol().getId())
                                    .flatMap(rol -> {
                                        user.setRol(rol);
                                        return Mono.just(user);
                                    })
                            );
                });
    }
}
