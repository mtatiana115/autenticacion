package co.com.bancolombia.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/v3/api-docs/",
                                "/swagger-ui.html",
                                "/swagger-ui/",
                                "/webjars/swagger-ui/**"
                        ).permitAll()
                        .anyExchange().permitAll()
                )
                .httpBasic(); // puedes usar .formLogin() si prefieres login por formulario
        return http.build();
    }
}
