package pl.miken.electionhandler.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.miken.electionhandler.dto.request.UserRequest;
import pl.miken.electionhandler.dto.response.UserResponse;
import pl.miken.electionhandler.exception.CodedException;
import pl.miken.electionhandler.mapper.UserMapper;
import pl.miken.electionhandler.service.UserService;

import java.util.List;

import static pl.miken.electionhandler.enumeration.ErrorCodes.USER_WITH_ID_NOT_FOUND;
import static pl.miken.electionhandler.enumeration.ErrorCodes.USER_WITH_PERSONAL_NUMBER_ALREADY_EXITS;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserResponse getUserData(Long userId) {
        var user = userService.findActiveUserById(userId).orElseThrow(
                () -> new CodedException(USER_WITH_ID_NOT_FOUND,
                        USER_WITH_ID_NOT_FOUND.getMessage().formatted(userId))
        );

        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllActiveUsers() {
        return userService.findAllActiveUsers().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public void deactivateUser(Long userId) {
        var user = userService.findActiveUserById(userId).orElseThrow(() -> new CodedException(USER_WITH_ID_NOT_FOUND,
                USER_WITH_ID_NOT_FOUND.getMessage().formatted(userId))
        );
        user.setIsActive(false);
        userService.saveUser(user);
    }

    public void activateUser(Long userId) {
        var user = userService.findUserById(userId).orElseThrow(() -> new CodedException(USER_WITH_ID_NOT_FOUND,
                USER_WITH_ID_NOT_FOUND.getMessage().formatted(userId))
        );
        user.setIsActive(true);
        userService.saveUser(user);
    }

    public  UserResponse createUser(UserRequest userRequest) {
        var user = userService.findUserByPersonalNumber(userRequest.getPersonalNumber()).orElse(null);

        if (user == null) {
            user = userMapper.toUser(userRequest);
            user.setIsActive(true);
            user = userService.saveUser(user);

            return userMapper.toUserResponse(user);
        }

        if (user.getIsActive()) {
            throw new CodedException(USER_WITH_PERSONAL_NUMBER_ALREADY_EXITS,
                    USER_WITH_PERSONAL_NUMBER_ALREADY_EXITS.getMessage().formatted(userRequest.getPersonalNumber()));
        } else {
            user.setIsActive(true);
            user = userService.saveUser(user);

            return userMapper.toUserResponse(user);
        }
    }
}
