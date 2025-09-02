package co.com.bancolombia.api.mapper;

import co.com.bancolombia.model.rol.Rol;
import org.mapstruct.Mapper;

import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.dto.response.UserRecordResponse;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface UserDTOMapper {

    UserRecordResponse toResponse(User user);

    @Mapping(target = "rol", expression = "java(mapRolIdToRol(createUserDTO.rolId()))")
    @Mapping(target = "id", ignore = true)
    User toModel(CreateUserRecord createUserDTO);

    default Rol mapRolIdToRol(Integer rolId) {
        return Rol.builder().id(rolId).build();
    }
}

