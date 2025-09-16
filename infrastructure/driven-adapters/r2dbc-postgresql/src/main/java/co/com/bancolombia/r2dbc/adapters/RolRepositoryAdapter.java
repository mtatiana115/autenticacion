package co.com.bancolombia.r2dbc.adapters;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entities.RolEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repositories.RolReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RolRepositoryAdapter extends  ReactiveAdapterOperations<
        Rol,
        RolEntity,
        Integer,
        RolReactiveRepository
> implements RolRepository {

    public RolRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper,  RolEntity -> mapper.map(RolEntity,Rol.class));

    }

}
