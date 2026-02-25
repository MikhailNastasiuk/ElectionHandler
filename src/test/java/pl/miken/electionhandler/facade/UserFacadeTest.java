package pl.miken.electionhandler.facade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.dto.request.UserRequest;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.enumeration.ErrorCodes;
import pl.miken.electionhandler.exception.CodedException;
import pl.miken.electionhandler.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserFacadeTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldPersistNewActiveUser() {
        var request = UserRequest.builder()
                .firstName("John")
                .lastName("Johnson")
                .email("john.johnson@example.com")
                .personalNumber("123456789012")
                .age(28)
                .build();

        var response = userFacade.createUser(request);

        assertNotNull(response.getId());
        var created = userRepository.findById(response.getId()).orElseThrow();
        assertTrue(created.getIsActive());
        assertEquals("123456789012", created.getPersonalNumber());
    }

    @Test
    void createUser_ShouldThrowWhenActiveUserWithSamePersonalNumberExists() {
        userRepository.save(buildUser("123456789013", true));
        var request = UserRequest.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .personalNumber("123456789013")
                .age(31)
                .build();

        var exception = assertThrows(CodedException.class, () -> userFacade.createUser(request));

        assertEquals(ErrorCodes.USER_WITH_PERSONAL_NUMBER_ALREADY_EXITS, exception.getErrorCode());
    }

    @Test
    void deactivateAndActivateUser_ShouldUpdateUserState() {
        var user = userRepository.save(buildUser("123456789014", true));

        userFacade.deactivateUser(user.getId());
        assertFalse(userRepository.findById(user.getId()).orElseThrow().getIsActive());

        userFacade.activateUser(user.getId());
        assertTrue(userRepository.findById(user.getId()).orElseThrow().getIsActive());
    }

    @Test
    void getAllActiveUsers_ShouldReturnOnlyActiveUsers() {
        userRepository.save(buildUser("123456789015", true));
        userRepository.save(buildUser("123456789016", false));

        List<?> users = userFacade.getAllActiveUsers();

        assertEquals(1, users.size());
    }

    private User buildUser(String personalNumber, boolean isActive) {
        return User.builder()
                .firstName("Test")
                .lastName("User")
                .email(personalNumber + "@example.com")
                .personalNumber(personalNumber)
                .age(30)
                .isActive(isActive)
                .build();
    }
}
