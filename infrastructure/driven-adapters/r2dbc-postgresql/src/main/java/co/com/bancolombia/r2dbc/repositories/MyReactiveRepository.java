package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.model.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.bancolombia.r2dbc.entities.UserEntity;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {

   Mono<Void> deleteById(String id);
   Mono<Boolean> existsByEmail(String email);
//   Mono<User> findByEmail(String email);
    @Query("SELECT u.*, r.id AS role_id, r.name AS role_name, r.description AS role_description " +
            "FROM users u " +
            "JOIN roles r ON u.role_id = r.id " +
            "WHERE u.email = :email")
    Mono<UserEntity> findByEmail(@Param("email") String email);
}
