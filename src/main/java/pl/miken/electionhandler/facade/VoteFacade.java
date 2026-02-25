package pl.miken.electionhandler.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.dto.VoteOptionPayload;
import pl.miken.electionhandler.dto.response.VoteResponse;
import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.entity.VoteOption;
import pl.miken.electionhandler.exception.CodedException;
import pl.miken.electionhandler.mapper.VoteMapper;
import pl.miken.electionhandler.service.ActiveVotingService;
import pl.miken.electionhandler.service.ObjectMapperService;
import pl.miken.electionhandler.service.VoteService;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;
import static pl.miken.electionhandler.enumeration.ErrorCodes.CANT_CHANGE_VOTE;
import static pl.miken.electionhandler.enumeration.ErrorCodes.CANT_START_NEW_VOTE;
import static pl.miken.electionhandler.enumeration.ErrorCodes.ACTIVE_VOTE_WITH_IDENTIFIER_NOT_FOUND;
import static pl.miken.electionhandler.enumeration.ErrorCodes.VOTE_WITH_IDENTIFIER_ALREADY_EXITS;
import static pl.miken.electionhandler.enumeration.ErrorCodes.VOTE_WITH_IDENTIFIER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteFacade {

    private final VoteService voteService;
    private final VoteMapper voteMapper;
    private final ActiveVotingService activeVotingService;
    private final ObjectMapperService objectMapperService;

    public VoteDataPayload findVoteByUniqueIdentifier(String uniqueIdentifier) {
        var vote = getVoteByUniqueIdentifier(uniqueIdentifier);

        return voteMapper.toPayload(buildNewVote(vote));
    }

    public VoteDataPayload findActiveByUniqueIdentifier(String uniqueIdentifier) {
        var vote = getVoteByUniqueIdentifier(uniqueIdentifier);
        var activeVoting = activeVotingService.getActiveVoting(vote).orElseThrow(
                () -> new CodedException(ACTIVE_VOTE_WITH_IDENTIFIER_NOT_FOUND,
                        ACTIVE_VOTE_WITH_IDENTIFIER_NOT_FOUND.getMessage().formatted(uniqueIdentifier))
        );

        var errorMessage = "Error reading data from active vote with identifier = " + uniqueIdentifier;
        return objectMapperService.deserializeObject(activeVoting.getVoteJson(), VoteDataPayload.class, errorMessage);
    }

    public List<VoteDataPayload> findAllActiveVote() {
        var activeVoting = activeVotingService.getAllActiveVotings();

        if (activeVoting.isEmpty()) {
            return List.of();
        }

        var errorMessage = "Error reading data from active voting";
        return activeVoting.stream()
                .map(av -> objectMapperService.deserializeObject(av.getVoteJson(), VoteDataPayload.class, errorMessage))
                .toList();
    }

    public List<VoteResponse> getAllActiveVotes() {
        return voteService.findAllActiveVotes().stream()
                .map(voteMapper::toResponse)
                .toList();
    }

    public void startVote(String uniqueIdentifier) {
        var vote = getVoteByUniqueIdentifier(uniqueIdentifier);
        var activeVoting = activeVotingService.getActiveVoting(vote);
        if (activeVoting.isPresent()) {
            throw new CodedException(CANT_START_NEW_VOTE, CANT_START_NEW_VOTE.getMessage().formatted(uniqueIdentifier));
        }

        var payload = voteMapper.toPayload(buildNewVote(vote));
        var errorMessage = "Error creating data from vote with identifier = " + uniqueIdentifier;
        var payloadJson = objectMapperService.serializeObject(payload, errorMessage);
        var newVote = ActiveVoting.builder()
                .vote(vote)
                .startedDate(LocalDate.now())
                .voteVersion(vote.getVersion())
                .voteJson(payloadJson)
                .build();
        activeVotingService.saveActiveVoting(newVote);
    }

    public void removeVote(String uniqueIdentifier) {
        var vote = getVoteByUniqueIdentifier(uniqueIdentifier);
        var activeVoting = activeVotingService.getActiveVoting(vote);
        if (activeVoting.isPresent()) {
            var errorMessage = "Vote with identifier = [" + uniqueIdentifier + "] has active voting";
            throw new CodedException(CANT_CHANGE_VOTE, CANT_CHANGE_VOTE.getMessage().formatted(errorMessage));
        }

        vote.getOptions().forEach(option -> option.setIsArchived(true));
        vote.setIsArchived(true);
        voteService.save(vote);
    }

    public void addVote(VoteDataPayload voteDataPayload) {
        var vote = voteService.findByUniqueIdentifier(voteDataPayload.getUniqueIdentifier());
        if (vote.isPresent()) {
            throw new CodedException(VOTE_WITH_IDENTIFIER_ALREADY_EXITS,
                    VOTE_WITH_IDENTIFIER_ALREADY_EXITS.getMessage().formatted(voteDataPayload.getUniqueIdentifier()));
        }

        var newVote = voteMapper.toEntity(voteDataPayload);
        newVote.setVersion(1);
        newVote.setIsArchived(false);

        newVote.getOptions().forEach(voteOption -> {
            voteOption.setIsArchived(false);
            voteOption.setVote(newVote);
        });

        voteService.save(newVote);
    }

    public void addOptionToVote(String uniqueIdentifier, VoteOptionPayload optionPayload) {
        var vote = getVoteAndCheckIsActive(uniqueIdentifier);

        var options = vote.getOptions();
        var isOptionExists = options.stream()
                .anyMatch(option -> option.getOptionName().equals(optionPayload.getOptionName()));
        if (isOptionExists) {
            var errorMessage = "The vote with identifier = [" + uniqueIdentifier + "] already has such option: " + optionPayload.getOptionName();
            throw new CodedException(CANT_CHANGE_VOTE, CANT_CHANGE_VOTE.getMessage().formatted(errorMessage));
        }
        options.add(VoteOption.builder()
                .vote(vote)
                .optionName(optionPayload.getOptionName())
                .isArchived(false)
                .build());

        vote.setVersion(vote.getVersion() + 1);
        voteService.save(vote);
    }

    public void removeOptionFromVote(String uniqueIdentifier, VoteOptionPayload optionPayload) {
        if (isNull(optionPayload.getId())) {
            var errorMessage = "Option id can't be null";
            throw new CodedException(CANT_CHANGE_VOTE, CANT_CHANGE_VOTE.getMessage().formatted(errorMessage));
        }

        var vote = getVoteAndCheckIsActive(uniqueIdentifier);
        var option = vote.getOptions().stream()
                .filter(o -> o.getId().equals(optionPayload.getId()))
                .findFirst()
                .orElse(null);
        if (isNull(option)) {
            var errorMessage = "No option with id = [" + optionPayload.getId() + "] found";
            throw new CodedException(CANT_CHANGE_VOTE, CANT_CHANGE_VOTE.getMessage().formatted(errorMessage));
        }
        option.setIsArchived(true);

        vote.setVersion(vote.getVersion() + 1);
        voteService.save(vote);
    }

    private Vote buildNewVote(Vote vote) {
        return Vote.builder()
                .id(vote.getId())
                .voteName(vote.getVoteName())
                .uniqueIdentifier(vote.getUniqueIdentifier())
                .description(vote.getDescription())
                .durationPeriodInDays(vote.getDurationPeriodInDays())
                .isAdult(vote.getIsAdult())
                .isMultiple(vote.getIsMultiple())
                .options(buildOptions(vote.getOptions()))
                .build();
    }

    private List<VoteOption> buildOptions(List<VoteOption> options) {
        return options.stream()
                .filter(option -> Boolean.FALSE.equals(option.getIsArchived()))
                .toList();
    }

    private Vote getVoteByUniqueIdentifier(String uniqueIdentifier) {
        return voteService.findByUniqueIdentifier(uniqueIdentifier).orElseThrow(
                () -> new CodedException(VOTE_WITH_IDENTIFIER_NOT_FOUND,
                        VOTE_WITH_IDENTIFIER_NOT_FOUND.getMessage().formatted(uniqueIdentifier))
        );
    }

    private Vote getVoteAndCheckIsActive(String uniqueIdentifier) {
        var vote = getVoteByUniqueIdentifier(uniqueIdentifier);
        var activeVoting = activeVotingService.getActiveVoting(vote);
        if (activeVoting.isPresent()) {
            var errorMessage = "The vote with identifier = [" + uniqueIdentifier + "] is in process.";
            throw new CodedException(CANT_CHANGE_VOTE, CANT_CHANGE_VOTE.getMessage().formatted(errorMessage));
        }

        return vote;
    }
}
