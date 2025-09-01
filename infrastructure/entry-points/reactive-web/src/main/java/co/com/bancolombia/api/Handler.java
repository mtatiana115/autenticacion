package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.mapper.UserDTOMapper;
import co.com.bancolombia.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final RequestValidator requestValidator;
    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> saveUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserRecord.class)
                .doOnNext(req -> log.info("Request received: {}", req))
                .flatMap(requestValidator::validateUser)
                .map(userDTOMapper::toModel)
                .doOnNext(user -> log.debug("Mapped to domain model: {}", user))
                .flatMap(userUseCase::saveUser)
                .doOnSuccess(saved -> log.info("User successfully saved: {}", saved))
                .doOnError(err -> log.error("Error while saving user", err))
                .flatMap(savedUser ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userDTOMapper.toResponse(savedUser))
                                .as(transactionalOperator::transactional)
                )
                .log("SaveUserFlow");
    }

}
