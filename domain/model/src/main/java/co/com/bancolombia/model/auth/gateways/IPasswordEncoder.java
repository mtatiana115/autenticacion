package co.com.bancolombia.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface IPasswordEncoder {
    Mono<String> encode(String password);
    Mono<Boolean> matches (String password, String hash);
}
