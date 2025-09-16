package co.com.bancolombia.usecase.rol;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RolUseCaseTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolUseCase rolUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_WhenRolExists_ShouldReturnRol() {
        // Arrange
        Rol rol = Rol.builder()
                .id(1)
                .name("ADMIN")
                .build();

        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));

        // Act
        Mono<Rol> result = rolUseCase.getById(1);

        // Assert
        StepVerifier.create(result)
                .expectNext(rol)
                .verifyComplete();

        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    void getById_WhenRolDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(rolRepository.findById(99)).thenReturn(Mono.empty());

        // Act
        Mono<Rol> result = rolUseCase.getById(99);

        // Assert
        StepVerifier.create(result)
                .verifyComplete(); // no emite nada

        verify(rolRepository, times(1)).findById(99);
    }
}
