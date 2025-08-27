package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.mapper.UserDTOMapper;
import co.com.bancolombia.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
private final RequestValidator requestValidator;
private  final UserUseCase userUseCase;
private final UserDTOMapper userDTOMapper;

    // @Transactional //mover a driven adapter r2dbc    
    public Mono<ServerResponse> saveUseCase(ServerRequest serverRequest) {
        
        return serverRequest.bodyToMono(CreateUserRecord.class)
        .flatMap(requestValidator::validateUser)
        .map(userDTOMapper::toModel)
        .flatMap(userUseCase::saveUser)
        .flatMap(saveUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDTOMapper.toResponse(saveUser)));
    }

}
