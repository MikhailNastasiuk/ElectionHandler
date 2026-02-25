package pl.miken.electionhandler.facade;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.dto.VoteOptionPayload;
import pl.miken.electionhandler.dto.request.VotingRequest;
import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.entity.VoteAnswers;
import pl.miken.electionhandler.entity.VoteOption;
import pl.miken.electionhandler.enumeration.ErrorCodes;
import pl.miken.electionhandler.exception.CodedException;
import pl.miken.electionhandler.repository.ActiveVotingRepository;
import pl.miken.electionhandler.repository.UserRepository;
import pl.miken.electionhandler.repository.VoteAnswersRepository;
import pl.miken.electionhandler.repository.VoteRepository;
import pl.miken.electionhandler.service.ObjectMapperService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ActiveVotingFacadeTest {

    @Autowired
    private ActiveVotingFacade activeVotingFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ActiveVotingRepository activeVotingRepository;

    @Autowired
    private VoteAnswersRepository voteAnswersRepository;

    @Autowired
    private ObjectMapperService objectMapperService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void addVoting_ShouldSaveUserAnswer() {
        var user = userRepository.save(buildUser("90010111111", true));
        var vote = voteRepository.save(buildVote("PETS"));
        var firstOption = vote.getOptions().getFirst();
        createActiveVoting(vote);

        activeVotingFacade.addVoting(VotingRequest.builder()
                .voteIdentifier(vote.getUniqueIdentifier())
                .userId(user.getId())
                .optionId(firstOption.getId())
                .build());

        var activeVoting = activeVotingRepository.findByVoteAndFinishedDateIsNull(vote).orElseThrow();
        var savedAnswer = voteAnswersRepository.findByActiveVotingAndUser(activeVoting, user).orElseThrow();
        assertEquals(firstOption.getId(), savedAnswer.getVoteOption().getId());
    }

    @Test
    void addVoting_ShouldThrowWhenUserAlreadyVoted() {
        var user = userRepository.save(buildUser("90010122222", true));
        var vote = voteRepository.save(buildVote("MUSIC"));
        var option = vote.getOptions().getFirst();
        var activeVoting = createActiveVoting(vote);
        voteAnswersRepository.save(VoteAnswers.builder()
                .user(user)
                .activeVoting(activeVoting)
                .voteOption(option)
                .voteDate(LocalDate.now())
                .build());

        var exception = assertThrows(CodedException.class, () ->
                activeVotingFacade.addVoting(VotingRequest.builder()
                        .voteIdentifier(vote.getUniqueIdentifier())
                        .userId(user.getId())
                        .optionId(option.getId())
                        .build()));

        assertEquals(ErrorCodes.USER_ALREADY_VOTED, exception.getErrorCode());
    }

    @Test
    void getActiveVotingData_ShouldReturnAggregatedStats() {
        var user1 = userRepository.save(buildUser("90010133333", true));
        var user2 = userRepository.save(buildUser("90010144444", true));
        var user3 = userRepository.save(buildUser("90010155555", true));
        var vote = voteRepository.save(buildVote("BOOKS"));
        var activeVoting = createActiveVoting(vote);
        var optionA = vote.getOptions().getFirst();
        var optionB = vote.getOptions().get(1);

        voteAnswersRepository.save(VoteAnswers.builder()
                .user(user1)
                .activeVoting(activeVoting)
                .voteOption(optionA)
                .voteDate(LocalDate.now())
                .build());
        voteAnswersRepository.save(VoteAnswers.builder()
                .user(user2)
                .activeVoting(activeVoting)
                .voteOption(optionA)
                .voteDate(LocalDate.now())
                .build());
        voteAnswersRepository.save(VoteAnswers.builder()
                .user(user3)
                .activeVoting(activeVoting)
                .voteOption(optionB)
                .voteDate(LocalDate.now())
                .build());

        entityManager.flush();
        entityManager.clear();

        var result = activeVotingFacade.getActiveVotingData(vote.getUniqueIdentifier());

        assertEquals(vote.getUniqueIdentifier(), result.getVoteIdentifier());
        assertEquals(Map.of("Option A", 2, "Option B", 1), result.getVotingData());
    }

    private ActiveVoting createActiveVoting(Vote vote) {
        var payload = VoteDataPayload.builder()
                .voteName(vote.getVoteName())
                .uniqueIdentifier(vote.getUniqueIdentifier())
                .description(vote.getDescription())
                .durationPeriodInDays(vote.getDurationPeriodInDays())
                .isAdult(vote.getIsAdult())
                .isMultiple(vote.getIsMultiple())
                .options(vote.getOptions().stream()
                        .map(option -> VoteOptionPayload.builder()
                                .id(option.getId())
                                .optionName(option.getOptionName())
                                .build())
                        .toList())
                .build();

        return activeVotingRepository.save(ActiveVoting.builder()
                .vote(vote)
                .voteJson(objectMapperService.serializeObject(payload, "serialize test vote"))
                .startedDate(LocalDate.now())
                .voteVersion(vote.getVersion())
                .build());
    }

    private Vote buildVote(String uniqueIdentifier) {
        var vote = Vote.builder()
                .voteName("Test vote")
                .uniqueIdentifier(uniqueIdentifier)
                .description("Description")
                .durationPeriodInDays(10)
                .version(1)
                .isAdult(false)
                .isMultiple(false)
                .isArchived(false)
                .options(new ArrayList<>())
                .build();

        vote.getOptions().add(VoteOption.builder()
                .vote(vote)
                .optionName("Option A")
                .isArchived(false)
                .build());
        vote.getOptions().add(VoteOption.builder()
                .vote(vote)
                .optionName("Option B")
                .isArchived(false)
                .build());

        return vote;
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
