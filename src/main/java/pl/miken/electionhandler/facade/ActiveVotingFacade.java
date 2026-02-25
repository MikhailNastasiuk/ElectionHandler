package pl.miken.electionhandler.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.dto.VoteOptionPayload;
import pl.miken.electionhandler.dto.request.VotingRequest;
import pl.miken.electionhandler.dto.response.VotingDataResponse;
import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.entity.VoteAnswers;
import pl.miken.electionhandler.entity.VoteOption;
import pl.miken.electionhandler.exception.CodedException;
import pl.miken.electionhandler.service.ActiveVotingService;
import pl.miken.electionhandler.service.ObjectMapperService;
import pl.miken.electionhandler.service.UserService;
import pl.miken.electionhandler.service.VoteService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static pl.miken.electionhandler.enumeration.ErrorCodes.ACTIVE_VOTE_WITH_ID_NOT_FOUND;
import static pl.miken.electionhandler.enumeration.ErrorCodes.OPTION_WITH_ID_NOT_FOUND;
import static pl.miken.electionhandler.enumeration.ErrorCodes.USER_ALREADY_VOTED;
import static pl.miken.electionhandler.enumeration.ErrorCodes.USER_WITH_ID_NOT_FOUND;
import static pl.miken.electionhandler.enumeration.ErrorCodes.VOTE_WITH_IDENTIFIER_NOT_FOUND;

/**
 * Facade for managing active voting
 * @see UserService
 * @see VoteService
 * @see ActiveVotingService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveVotingFacade {

    private final UserService userService;
    private final VoteService voteService;
    private final ActiveVotingService activeVotingService;
    private final ObjectMapperService objectMapperService;

    /**
     * Add new voting from user
     * @param votingRequest request to create new voting
     */
    public void addVoting(VotingRequest votingRequest) {
        var user = getActiveUser(votingRequest.getUserId());
        var vote = getVoteByUniqueIdentifier(votingRequest.getVoteIdentifier());
        var activeVoting = getActiveVoting(vote);
        var payload = getVoteDataPayload(activeVoting.getVoteJson(), activeVoting.getId());
        var isOptionPresent = payload.getOptions().stream()
                .anyMatch(o -> o.getId().equals(votingRequest.getOptionId()));
        if (!isOptionPresent) {
            throw new CodedException(OPTION_WITH_ID_NOT_FOUND, OPTION_WITH_ID_NOT_FOUND.getMessage().formatted(votingRequest.getOptionId()));
        }

        var answer = activeVotingService.getUsersVote(user, activeVoting);
        if (answer.isPresent()) {
            throw new CodedException(USER_ALREADY_VOTED, USER_ALREADY_VOTED.getMessage().formatted(votingRequest.getUserId()));
        }

        activeVotingService.saveVoteAnswers(VoteAnswers.builder()
                .user(user)
                .activeVoting(activeVoting)
                .voteOption(VoteOption.builder().id(votingRequest.getOptionId()).build())
                .voteDate(LocalDate.now())
                .build());
    }

    /**
     * Get voting data:</br>
     * How many votes added to vote option
     * @param voteIdentifier unique vote identifier
     */
    public VotingDataResponse getActiveVotingData(String voteIdentifier) {
        var vote = getVoteByUniqueIdentifier(voteIdentifier);
        var activeVoting = getActiveVoting(vote);
        var payload = getVoteDataPayload(activeVoting.getVoteJson(), activeVoting.getId());
        var answers = activeVoting.getVoteAnswers();
        var options = payload.getOptions();

        Map<String, Integer> votingData = new HashMap<>();
        for (var answer: answers) {
            options.stream()
                    .filter(o -> o.getId().equals(answer.getVoteOption().getId()))
                    .findFirst()
                    .map(VoteOptionPayload::getOptionName)
                    .ifPresent(key -> votingData.merge(key, 1, Integer::sum));
        }

        return VotingDataResponse.builder()
                .voteIdentifier(voteIdentifier)
                .votingData(votingData)
                .build();
    }

    private Vote getVoteByUniqueIdentifier(String uniqueIdentifier) {
        return voteService.findByUniqueIdentifier(uniqueIdentifier).orElseThrow(
                () -> new CodedException(VOTE_WITH_IDENTIFIER_NOT_FOUND,
                        VOTE_WITH_IDENTIFIER_NOT_FOUND.getMessage().formatted(uniqueIdentifier))
        );
    }

    private User getActiveUser(Long userId) {
        return userService.findActiveUserById(userId).orElseThrow(
                () -> new CodedException(USER_WITH_ID_NOT_FOUND,
                        USER_WITH_ID_NOT_FOUND.getMessage().formatted(userId))
        );
    }

    private ActiveVoting getActiveVoting(Vote vote) {
        return activeVotingService.getActiveVoting(vote).orElseThrow(
                () -> new CodedException(ACTIVE_VOTE_WITH_ID_NOT_FOUND,
                        ACTIVE_VOTE_WITH_ID_NOT_FOUND.getMessage().formatted(vote.getId()))
        );
    }

    private VoteDataPayload getVoteDataPayload(String json, Long votingId) {
        var errorMessage = "Error reading data from active vote with id = " + votingId;
        return objectMapperService.deserializeObject(json, VoteDataPayload.class, errorMessage);
    }
}
