package pl.miken.electionhandler.service;

import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.entity.VoteAnswers;

import java.util.List;
import java.util.Optional;

public interface ActiveVotingService {

    Optional<ActiveVoting> getActiveVoting(Vote vote);

    Optional<ActiveVoting> getActiveVoting(Long votingId);

    ActiveVoting saveActiveVoting(ActiveVoting activeVoting);

    List<ActiveVoting> getAllActiveVotings();

    Optional<VoteAnswers> getUsersVote(User user, ActiveVoting activeVoting);

    VoteAnswers saveVoteAnswers(VoteAnswers voteAnswers);
}
