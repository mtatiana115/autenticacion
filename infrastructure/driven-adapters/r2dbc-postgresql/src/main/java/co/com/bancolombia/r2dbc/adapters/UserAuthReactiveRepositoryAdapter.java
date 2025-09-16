package co.com.bancolombia.r2dbc.adapters;


import co.com.bancolombia.model.user.gateways.UserAuthRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.mapper.UserMapper;
import co.com.bancolombia.r2dbc.repositories.RolReactiveRepository;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import co.com.bancolombia.r2dbc.entities.RolEntity;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.repositories.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserAuthReactiveRepositoryAdapter implements UserAuthRepository {

    private final UserReactiveRepository userRepo;
    private final RolReactiveRepository rolRepo;
    private final UserMapper userMapper;

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepo.findByEmail(email)
                .flatMap(userEntity ->
                        rolRepo.findById(userEntity.getRolId())
                                .map(rolEntity -> {
                                    User user = userMapper.toModel(userEntity);
                                    user.setRol(userMapper.toRol(rolEntity)); // Mapeo explícito
                                    return user;
                                })
                                .switchIfEmpty(Mono.just(userMapper.toModel(userEntity))) // Maneja usuarios sin rol
                );
    }
}