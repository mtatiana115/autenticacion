package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.RequestValidator;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.mapper.UserDTOMapper;
import co.com.bancolombia.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {
    private final RequestValidator requestValidator;
    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> saveUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserRecord.class)
                // Logger 1: Confirma que el DTO con el rolId ha sido recibido
                .doOnNext(req -> log.info("1. DTO recibido en el handler: {}", req))
                .flatMap(requestValidator::validateUser)
                // Logger 2: Confirma que el DTO ha pasado la validación
                .doOnNext(validDto -> log.info("2. DTO después de la validación: {}", validDto))
                .map(userDTOMapper::toModel)
                // Logger 3: Confirma el estado del objeto de dominio 'User'
                .doOnNext(user -> log.info("3. Mapeado al modelo de dominio: {}", user))
                .flatMap(userUseCase::saveUser)
                .doOnSuccess(saved -> log.info("Usuario guardado exitosamente: {}", saved))
                .doOnError(err -> log.error("Error al guardar el usuario", err))
                .flatMap(savedUser ->
                        ServerResponse.status(HttpResponseStatus.CREATED.code())
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userDTOMapper.toResponse(savedUser))
                                .as(transactionalOperator::transactional)
                )
                .log("SaveUserFlow");
    }

    public Mono<ServerResponse> existsUserByEmailUseCase(ServerRequest serverRequest){
        String email = serverRequest.pathVariable("email");
        log.info("Checking if a user exists with email: {}", email);
        return userUseCase.existsUserByEmail(email)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"existsUser\": " + exists + "}"));
    }

    public Mono<ServerResponse> findUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        log.info("Finding user by email: {}", email);
        return userUseCase.findByEmail(email)
                .doOnNext(user -> log.debug("Found user: {}", user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDTOMapper.toResponse(user)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(err -> log.error("Error finding user by email", err))
                .log("FindUserByEmailFlow");
    }

//    public Mono<ServerResponse> findUserByEmail(ServerRequest serverRequest) {
//        String email = serverRequest.queryParam("email")
//                .orElseThrow(() -> new IllegalArgumentException("El parámetro 'email' es obligatorio"));
//
//        log.info("Finding user by email: {}", email);
//
//        return userUseCase.findByEmail(email)
//                .flatMap(user -> ServerResponse.ok()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(userDTOMapper.toResponse(user)))
//                .switchIfEmpty(ServerResponse.notFound().build());
//    }


}
