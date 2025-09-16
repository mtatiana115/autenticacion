package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.EmailAlreadyExistsException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserUseCase useCase;

    private final User user = User.builder()
            .id("1")
            .documentId("123456789")
            .name("Tatiana")
            .lastname("Maldonado")
            .birthDate(LocalDate.of(1995, 5, 12))
            .address("Calle Falsa 123")
            .email("tatiana@test.com")
            .phone("555-5555")
            .baseSalary(BigDecimal.valueOf(8000000.00))
            .build();

    @Test
    void saveUser_WhenEmailDoesNotExist_ShouldSaveUser() {
        when(repository.existsByEmail(any(String.class))).thenReturn(Mono.just(false));
        when(repository.save(any(User.class))).thenReturn(Mono.just(user));

        Mono<User> result = useCase.saveUser(user);

        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals(user.getEmail()))
                .verifyComplete();

        verify(repository).existsByEmail(user.getEmail());
        verify(repository).save(user);
    }

    @Test
    void saveUser_WhenEmailAlreadyExists_ShouldThrowException() {

        when(repository.existsByEmail(any(String.class))).thenReturn(Mono.just(true));

        Mono<User> result = useCase.saveUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EmailAlreadyExistsException &&
                        throwable.getMessage().contains(user.getEmail())
                )
                .verify();

        verify(repository).existsByEmail(user.getEmail());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void saveUser_WhenExistsByEmailFails_ShouldPropagateError() {
        when(repository.existsByEmail(any(String.class))).thenReturn(Mono.error(new RuntimeException("DB connection failed")));

        Mono<User> result = useCase.saveUser(user);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsByEmail(user.getEmail());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void saveUser_WhenSaveFails_ShouldPropagateError() {
        when(repository.existsByEmail(any(String.class))).thenReturn(Mono.just(false));
        when(repository.save(any(User.class))).thenReturn(Mono.error(new RuntimeException("Failed to save to database")));

        Mono<User> result = useCase.saveUser(user);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsByEmail(user.getEmail());
        verify(repository).save(user);
    }

    @Test
    void existsUserByEmail_WhenEmailExists_ShouldReturnTrue() {
        when(repository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        Mono<Boolean> result = useCase.existsUserByEmail("existing@test.com");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsUserByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        when(repository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        Mono<Boolean> result = useCase.existsUserByEmail("non-existing@test.com");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
