package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.ValidateTokenDTO;
import co.com.bancolombia.api.dto.response.UserRecordResponse;
import co.com.bancolombia.api.dto.response.ValidationTokenResponseDTO;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface ValidationTokenMapper {
    @Mapping(target = "rolName", source = "user.rol.name")
    ValidationTokenResponseDTO toResponse(User user);
}
