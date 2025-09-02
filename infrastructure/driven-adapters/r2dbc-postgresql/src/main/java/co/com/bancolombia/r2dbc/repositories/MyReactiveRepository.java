package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.model.user.User;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.bancolombia.r2dbc.entities.UserEntity;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {

   Mono<Void> deleteById(String id);
   Mono<Boolean> existsByEmail(String email);
   Mono<User> findByEmail(String email);
}
