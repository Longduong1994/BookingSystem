package booking.service.mapper;


import booking.dto.response.UserResponseDto;
import booking.entity.Role;
import booking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    @Mapping(target = "roles", source = "user.roles", qualifiedByName = "mapRolesToStrings")
    UserResponseDto toResponse(User user);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }
}
