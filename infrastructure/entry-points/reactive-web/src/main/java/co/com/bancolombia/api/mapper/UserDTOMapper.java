package co.com.bancolombia.api.mapper;

import org.mapstruct.Mapper;

import co.com.bancolombia.api.dto.request.CreateUserRecord;
import co.com.bancolombia.api.dto.response.UserRecordResponse;
import co.com.bancolombia.model.user.User;

@Mapper(componentModel="spring")
public interface UserDTOMapper {

    UserRecordResponse toResponse(User user);

    User toModel(CreateUserRecord createUserDTO);
}
