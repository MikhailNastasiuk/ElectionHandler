package pl.miken.electionhandler.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.repository.UserRepository;
import pl.miken.electionhandler.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllActiveUsers() {
        return userRepository.findAllByIsActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findActiveUserById(Long userId) {
        return userRepository.findByIdAndIsActiveTrue(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByPersonalNumber(String personalNumber) {
        return userRepository.findAllByPersonalNumber(personalNumber);
    }
}
