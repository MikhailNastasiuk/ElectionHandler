package pl.miken.electionhandler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.miken.electionhandler.config.MapperConfig;
import pl.miken.electionhandler.dto.request.UserRequest;
import pl.miken.electionhandler.dto.response.UserResponse;
import pl.miken.electionhandler.entity.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserResponse toUserResponse(User user);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "isActive")
    @Mapping(ignore = true, target = "voteAnswers")
    User toUser(UserRequest userRequest);
}
