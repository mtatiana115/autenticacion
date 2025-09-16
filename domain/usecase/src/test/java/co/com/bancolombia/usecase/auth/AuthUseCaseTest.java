package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.auth.gateways.IAuthProvider;
import co.com.bancolombia.model.auth.gateways.IPasswordEncoder;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserAuthRepository;
import co.com.bancolombia.usecase.rol.RolUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private IAuthProvider authProvider;

    @Mock
    private RolUseCase rolUseCase;

    @Mock
    private IPasswordEncoder passwordEncoder;

    @Mock
    private UserAuthRepository userAuthRepository;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User validUser;
    private Rol validRol;
    private Auth validAuth;

    @BeforeEach
    void setUp() {
        validRol = Rol.builder()
                .id(1)
                .name("USER")
                .description("Standard user role")
                .build();

        validUser = User.builder()
                .id(UUID.randomUUID())
                .documentId("12345678")
                .name("John")
                .lastname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .email("user@example.com")
                .phone("1234567890")
                .baseSalary(new BigDecimal("5000"))
                .rol(validRol)
                .password("$2a$10$hashedpassword")
                .build();

        validAuth = Auth.builder()
                .token("jwt-token-123")
                .build();
    }

    @Test
    void signIn_WhenValidCredentials_ShouldReturnAuth() {
        // Arrange
        String email = "user@example.com";
        String password = "plainPassword";

        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches(password, validUser.getPassword())).thenReturn(Mono.just(true));
        when(authProvider.generateToken(validUser)).thenReturn(Mono.just(validAuth));

        // Act
        Mono<Auth> result = authUseCase.signIn(email, password);

        // Assert
        StepVerifier.create(result)
                .expectNext(validAuth)
                .verifyComplete();

        verify(userAuthRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, validUser.getPassword());
        verify(authProvider).generateToken(validUser);
    }

    @Test
    void signIn_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "plainPassword";

        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.empty());

        // Act
        Mono<Auth> result = authUseCase.signIn(email, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                "Credenciales inválidas".equals(throwable.getMessage()))
                .verify();

        verify(userAuthRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authProvider, never()).generateToken(any(User.class));
    }

    @Test
    void signIn_WhenPasswordDoesNotMatch_ShouldThrowException() {
        // Arrange
        String email = "user@example.com";
        String password = "wrongPassword";

        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches(password, validUser.getPassword())).thenReturn(Mono.just(false));

        // Act
        Mono<Auth> result = authUseCase.signIn(email, password);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                "Credenciales inválidas".equals(throwable.getMessage()))
                .verify();

        verify(userAuthRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, validUser.getPassword());
        verify(authProvider, never()).generateToken(any(User.class));
    }

    @Test
    void signIn_WhenPasswordEncoderThrowsException_ShouldPropagateException() {
        // Arrange
        String email = "user@example.com";
        String password = "plainPassword";

        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches(password, validUser.getPassword()))
                .thenReturn(Mono.error(new RuntimeException("Encoder error")));

        // Act
        Mono<Auth> result = authUseCase.signIn(email, password);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userAuthRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, validUser.getPassword());
        verify(authProvider, never()).generateToken(any(User.class));
    }

    @Test
    void signIn_WhenAuthProviderThrowsException_ShouldPropagateException() {
        // Arrange
        String email = "user@example.com";
        String password = "plainPassword";

        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.just(validUser));
        when(passwordEncoder.matches(password, validUser.getPassword())).thenReturn(Mono.just(true));
        when(authProvider.generateToken(validUser))
                .thenReturn(Mono.error(new RuntimeException("Token generation failed")));

        // Act
        Mono<Auth> result = authUseCase.signIn(email, password);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userAuthRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, validUser.getPassword());
        verify(authProvider).generateToken(validUser);
    }

    @Test
    void validateToken_WhenValidToken_ShouldReturnUser() {
        // Arrange
        String token = "valid-jwt-token";
        String email = "user@example.com";

        when(authProvider.validateToken(token)).thenReturn(Mono.just(true));
        when(authProvider.getSubject(token)).thenReturn(Mono.just(email));
        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.just(validUser));

        // Act
        Mono<User> result = authUseCase.validateToken(token);

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(authProvider).validateToken(token);
        verify(authProvider).getSubject(token);
        verify(userAuthRepository).findByEmail(email);
    }

    @Test
    void validateToken_WhenTokenIsInvalid_ShouldThrowException() {
        // Arrange
        String token = "invalid-jwt-token";

        when(authProvider.validateToken(token)).thenReturn(Mono.just(false));

        // Act
        Mono<User> result = authUseCase.validateToken(token);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                "Token inválido".equals(throwable.getMessage()))
                .verify();

        verify(authProvider).validateToken(token);
        verify(authProvider, never()).getSubject(anyString());
        verify(userAuthRepository, never()).findByEmail(anyString());
    }

    @Test
    void validateToken_WhenValidationThrowsException_ShouldPropagateException() {
        // Arrange
        String token = "jwt-token";

        when(authProvider.validateToken(token))
                .thenReturn(Mono.error(new RuntimeException("Validation service down")));

        // Act
        Mono<User> result = authUseCase.validateToken(token);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(authProvider).validateToken(token);
        verify(authProvider, never()).getSubject(anyString());
        verify(userAuthRepository, never()).findByEmail(anyString());
    }

    @Test
    void validateToken_WhenGetSubjectThrowsException_ShouldPropagateException() {
        // Arrange
        String token = "jwt-token";

        when(authProvider.validateToken(token)).thenReturn(Mono.just(true));
        when(authProvider.getSubject(token))
                .thenReturn(Mono.error(new RuntimeException("Cannot extract subject")));

        // Act
        Mono<User> result = authUseCase.validateToken(token);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(authProvider).validateToken(token);
        verify(authProvider).getSubject(token);
        verify(userAuthRepository, never()).findByEmail(anyString());
    }

    @Test
    void validateToken_WhenUserNotFoundByEmail_ShouldThrowException() {
        // Arrange
        String token = "valid-jwt-token";
        String email = "nonexistent@example.com";

        when(authProvider.validateToken(token)).thenReturn(Mono.just(true));
        when(authProvider.getSubject(token)).thenReturn(Mono.just(email));
        when(userAuthRepository.findByEmail(email)).thenReturn(Mono.empty());

        // Act
        Mono<User> result = authUseCase.validateToken(token);

        // Assert
        StepVerifier.create(result)
                .expectComplete() // Mono.empty() completa sin emitir elementos
                .verify();

        verify(authProvider).validateToken(token);
        verify(authProvider).getSubject(token);
        verify(userAuthRepository).findByEmail(email);
    }

    @Test
    void validateToken_WhenUserRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        String token = "valid-jwt-token";
        String email = "user@example.com";

        when(authProvider.validateToken(token)).thenReturn(Mono.just(true));
        when(authProvider.getSubject(token)).thenReturn(Mono.just(email));
        when(userAuthRepository.findByEmail(email))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // Act
        Mono<User> result = authUseCase.validateToken(token);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(authProvider).validateToken(token);
        verify(authProvider).getSubject(token);
        verify(userAuthRepository).findByEmail(email);
    }
}