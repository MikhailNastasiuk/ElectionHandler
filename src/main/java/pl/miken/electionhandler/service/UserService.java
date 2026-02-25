package pl.miken.electionhandler.service;

import pl.miken.electionhandler.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAllActiveUsers();

    Optional<User> findActiveUserById(Long userId);

    Optional<User> findUserById(Long userId);

    Optional<User> findUserByPersonalNumber(String email);

    User saveUser(User user);
}
