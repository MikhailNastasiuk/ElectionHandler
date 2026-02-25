package pl.miken.electionhandler.facade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.dto.VoteOptionPayload;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.entity.VoteOption;
import pl.miken.electionhandler.enumeration.ErrorCodes;
import pl.miken.electionhandler.exception.CodedException;
import pl.miken.electionhandler.repository.ActiveVotingRepository;
import pl.miken.electionhandler.repository.VoteRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VoteFacadeTest {

    @Autowired
    private VoteFacade voteFacade;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ActiveVotingRepository activeVotingRepository;

    @Test
    void addVote_ShouldSaveVoteWithVersionAndOptions() {
        var payload = VoteDataPayload.builder()
                .voteName("Favorite Color")
                .uniqueIdentifier("COLOR")
                .description("Choose your favorite color")
                .durationPeriodInDays(7)
                .isAdult(false)
                .isMultiple(false)
                .options(List.of(
                        VoteOptionPayload.builder().optionName("Blue").build(),
                        VoteOptionPayload.builder().optionName("Green").build()
                ))
                .build();

        voteFacade.addVote(payload);

        var savedVote = voteRepository.findByUniqueIdentifierAndIsArchivedFalse("COLOR").orElseThrow();
        assertEquals(1, savedVote.getVersion());
        assertFalse(savedVote.getIsArchived());
        assertEquals(2, savedVote.getOptions().size());
        assertTrue(savedVote.getOptions().stream().allMatch(o -> Boolean.FALSE.equals(o.getIsArchived())));
    }

    @Test
    void startVote_ShouldCreateActiveVoting() {
        var vote = voteRepository.save(buildVote("CITY"));

        voteFacade.startVote(vote.getUniqueIdentifier());

        var activeVoting = activeVotingRepository.findByVoteAndFinishedDateIsNull(vote).orElseThrow();
        assertEquals(vote.getVersion(), activeVoting.getVoteVersion());
        assertTrue(activeVoting.getVoteJson().contains("\"uniqueIdentifier\":\"CITY\""));
    }

    @Test
    void addOptionToVote_ShouldAddOptionAndIncreaseVersion() {
        var vote = voteRepository.save(buildVote("FOOD"));
        var initialVersion = vote.getVersion();

        voteFacade.addOptionToVote(vote.getUniqueIdentifier(), VoteOptionPayload.builder().optionName("Pizza").build());

        var changedVote = voteRepository.findByUniqueIdentifierAndIsArchivedFalse("FOOD").orElseThrow();
        assertEquals(initialVersion + 1, changedVote.getVersion());
        assertTrue(changedVote.getOptions().stream().anyMatch(o -> "Pizza".equals(o.getOptionName())));
    }

    @Test
    void removeOptionFromVote_ShouldArchiveOptionAndIncreaseVersion() {
        var vote = voteRepository.save(buildVote("SPORT"));
        var option = vote.getOptions().getFirst();
        var initialVersion = vote.getVersion();

        voteFacade.removeOptionFromVote(
                vote.getUniqueIdentifier(),
                VoteOptionPayload.builder().id(option.getId()).build()
        );

        var changedVote = voteRepository.findByUniqueIdentifierAndIsArchivedFalse("SPORT").orElseThrow();
        var archivedOption = changedVote.getOptions().stream()
                .filter(o -> o.getId().equals(option.getId()))
                .findFirst()
                .orElseThrow();
        assertTrue(archivedOption.getIsArchived());
        assertEquals(initialVersion + 1, changedVote.getVersion());
    }

    @Test
    void removeOptionFromVote_ShouldThrowWhenOptionIdIsNull() {
        var vote = voteRepository.save(buildVote("MOVIE"));

        var exception = assertThrows(CodedException.class, () ->
                voteFacade.removeOptionFromVote(vote.getUniqueIdentifier(), VoteOptionPayload.builder().build()));

        assertEquals(ErrorCodes.CANT_CHANGE_VOTE, exception.getErrorCode());
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
}
