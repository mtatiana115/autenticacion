package co.com.bancolombia.jwtadapter;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .flatMap(auth -> {
                    Object credentials = auth.getCredentials();
                    if (credentials == null) {
                        return Mono.error(new Exception("Invalid authentication credentials: token is missing"));
                    }
                    String token = credentials.toString();
                    return Mono.just(token);
                })
                .log()
                .onErrorResume(e -> Mono.error(new Throwable("bad token", e)))
                .map(token -> {
                    Map<String, Object> claims = jwtProvider.getClaims(token);
                    String role = (String) claims.get("role");
                    String subject = (String) claims.get("sub");
                    return new UsernamePasswordAuthenticationToken(
                            subject,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                });
    }
}

