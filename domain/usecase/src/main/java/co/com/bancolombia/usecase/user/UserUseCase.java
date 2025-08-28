package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.EmailAlreadyExistsException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase  {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user){
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(existe -> existe
                        ? Mono.error(new EmailAlreadyExistsException(user.getEmail()))
                        : Mono.just(user))
                .flatMap(userRepository::save);
    }
}
