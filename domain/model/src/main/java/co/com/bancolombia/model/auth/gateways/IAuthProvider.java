package co.com.bancolombia.model.auth.gateways;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface IAuthProvider {
    Mono<Auth> generateToken(User user);

    Mono<Boolean> validateToken(Auth token);

}
