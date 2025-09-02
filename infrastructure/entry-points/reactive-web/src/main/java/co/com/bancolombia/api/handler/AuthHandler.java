package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.SignInDTO;
import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.auth.gateways.IAuthProvider;
import co.com.bancolombia.usecase.auth.AuthUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {
    private final AuthUseCase authUseCase;

    public Mono<ServerResponse> listenSignIn (ServerRequest serverRequest){
        return serverRequest.bodyToMono(SignInDTO.class)
                .flatMap(credentials -> authUseCase.signIn(credentials.email(), credentials.password())
                        .flatMap(auth ->
                                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(auth), Auth.class)));
    }

}
