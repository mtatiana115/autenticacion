package co.com.bancolombia.config;

import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import co.com.bancolombia.r2dbc.entities.RolEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import co.com.bancolombia.r2dbc.repositories.UserReactiveRepository;
import co.com.bancolombia.r2dbc.repositories.RolReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RolReactiveRepository rolRepository;
    private final UserReactiveRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seeder() {
        return args -> {
            initializeRoles()
                    .then(initializeAdminUser())
                    .subscribe(
                            v -> log.info("Data initialization completed successfully"),
                            error -> log.error("Error during data initialization: {}", error.getMessage())
                    );
        };
    }

    private Mono<Void> initializeRoles() {
        List<RolEntity> defaultRoles = Arrays.asList(
                new RolEntity(1, "ADMIN", "Administrator with full access"),
                new RolEntity(2, "ADVISOR", "Financial advisor role"),
                new RolEntity(3, "CLIENT", "Client role")
        );

        return Flux.fromIterable(defaultRoles)
                .flatMap(role ->
                        rolRepository.findById(role.getId())
                                .hasElement()
                                .flatMap(exists -> {
                                    if (!exists) {
                                        return rolRepository.save(role)
                                                .doOnSuccess(saved -> log.info("Role created: {}", saved.getName()))
                                                .then();
                                    } else {
                                        log.info("Role already exists: {}", role.getName());
                                        return Mono.empty();
                                    }
                                })
                )
                .then()
                .doOnSuccess(v -> log.info("Roles verification completed"));
    }

    private Mono<Void> initializeAdminUser() {
        return rolRepository.findById(1)
                .flatMap(adminRole ->
                        userRepository.findByEmail("admin@example.com")
                                .hasElement()
                                .flatMap(userExists -> {
                                    if (userExists) {
                                        log.info("Admin user already exists");
                                        return Mono.empty();
                                    } else {
                                        return passwordEncoder.encode("esunacontrasena")
                                                .flatMap(encodedPassword -> {
                                                    UserEntity adminUser = UserEntity.builder()
                                                            .id(null)
                                                            .documentId("1234567890")
                                                            .name("Admin")
                                                            .lastname("System")
                                                            .birthDate(LocalDate.of(1990, 1, 1))
                                                            .address("Admin Address")
                                                            .email("admin@example.com")
                                                            .phone("+573001234567")
                                                            .baseSalary(BigDecimal.valueOf(5000000))
                                                            .rolId(1)
                                                            .password(encodedPassword)
                                                            .build();

                                                    return userRepository.save(adminUser)
                                                            .doOnSuccess(user -> log.info("✅ Admin user created with id: {}", user.getId()))
                                                            .doOnError(error -> log.warn("❌ Could not create admin user: {}", error.getMessage()))
                                                            .then();
                                                });
                                    }
                                })
                )
                .onErrorResume(error -> {
                    log.warn("Could not create admin user: {}", error.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}
