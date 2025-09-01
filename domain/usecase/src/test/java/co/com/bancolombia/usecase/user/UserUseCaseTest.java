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
            .userId("1")
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
}
