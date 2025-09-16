package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface UserAuthRepository {
    Mono<User> findByEmail(String email);
}
