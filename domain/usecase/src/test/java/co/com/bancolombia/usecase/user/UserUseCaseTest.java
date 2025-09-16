package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exception.EmailAlreadyExistsException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_WhenEmailDoesNotExist_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("plainPassword")).thenReturn(Mono.just("hashed-password"));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // Act & Assert
        StepVerifier.create(userUseCase.saveUser(user))
                .expectNextMatches(savedUser ->
                        savedUser.getPassword().equals("hashed-password")
                )
                .verifyComplete();

        verify(passwordEncoder).encode("plainPassword"); // ✅ ajustado
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUser_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof EmailAlreadyExistsException &&
                                throwable.getMessage().contains(user.getEmail())
                )
                .verify();

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void saveUser_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("plainPassword")).thenReturn(Mono.just("hashed-password"));
        when(userRepository.save(any(User.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("DB error")
                )
                .verify();

        verify(passwordEncoder).encode("plainPassword"); // ✅ ajustado
        verify(userRepository).save(any(User.class));
    }

    @Test
    void existsUserByEmail_ShouldReturnBoolean() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.existsUserByEmail("test@example.com"))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByEmail("test@example.com"))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByEmail("test@example.com");
    }
}
