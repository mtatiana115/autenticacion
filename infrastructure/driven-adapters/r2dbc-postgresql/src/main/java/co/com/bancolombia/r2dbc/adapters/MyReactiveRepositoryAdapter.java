package co.com.bancolombia.r2dbc.adapters;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.mapper.UserMapper;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repositories.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MyReactiveRepositoryAdapter implements UserRepository{

    private final UserReactiveRepository repository;
    private final UserMapper mapper;

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .switchIfEmpty(Mono.empty())
                .map(mapper::toModel)
                .doOnNext(user -> log.info("USUARIODB *********************{}",user));
    }

    @Override
    public Flux<String> findAdminEmails(Integer rolId) {
        return repository.findAdminEmails(rolId);
    }

    @Override
    public Mono<User> save(User user){
        return repository.save(mapper.toEntity(user))
                .map(mapper::toModel);
    }

    @Override
    public Mono<User> findById(String id){
        return repository.findById(id).map(mapper::toModel);
    }
}