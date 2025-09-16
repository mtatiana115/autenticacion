package co.com.bancolombia.model.rol.gateways;

import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface RolRepository {
    public Mono<Rol> findById(Integer id);
}
