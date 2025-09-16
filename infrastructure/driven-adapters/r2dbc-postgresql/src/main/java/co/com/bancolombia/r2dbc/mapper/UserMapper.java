package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entities.RolEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // User a UserEntity
    @Mapping(source = "rol.id", target = "rolId") // rol.id → rolId
    UserEntity toEntity(User user);

    // UserEntity a User
    @Mapping(source = "rolId", target = "rol", qualifiedByName = "rolFromId")
    User toModel(UserEntity userEntity);

    // RolEntity a Rol - Método que faltaba
    Rol toRol(RolEntity rolEntity);

    // RolEntity a Rol alternativo usando mapeo explícito
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Rol toModel(RolEntity rolEntity);

    @Named("rolFromId")
    default Rol rolFromId(Integer rolId) {
        if (rolId == null) {
            System.out.println("ENTRE AQUI**********************");
            return null;
        }
        return Rol.builder().id(rolId).build();
    }

}
