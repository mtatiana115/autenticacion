package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // User a UserEntity
    @Mapping(source = "rol.id", target = "rolId") // rol.id → rolId
    UserEntity toEntity(User user);

    // UserEntity a User
    @Mapping(source = "rolId", target = "rol", qualifiedByName = "rolFromId")
    User toModel(UserEntity userEntity);

    // Método auxiliar para crear Rol desde Id
    @Named("rolFromId")
    default Rol rolFromId(Integer rolId) {
        if (rolId == null) {
            return null;
        }
        return Rol.builder().id(rolId).build();
    }
}
