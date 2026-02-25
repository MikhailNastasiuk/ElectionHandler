package pl.miken.electionhandler.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.entity.VoteAnswers;
import pl.miken.electionhandler.repository.ActiveVotingRepository;
import pl.miken.electionhandler.repository.VoteAnswersRepository;
import pl.miken.electionhandler.service.ActiveVotingService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveVotingServiceImpl implements ActiveVotingService {

    private final ActiveVotingRepository activeVotingRepository;
    private final VoteAnswersRepository voteAnswersRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ActiveVoting> getActiveVoting(Vote vote) {
        return activeVotingRepository.findByVoteAndFinishedDateIsNull(vote);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActiveVoting> getActiveVoting(Long votingId) {
        return activeVotingRepository.findByIdAndFinishedDateIsNull(votingId);
    }

    @Override
    @Transactional
    public ActiveVoting saveActiveVoting(ActiveVoting activeVoting) {
        return activeVotingRepository.save(activeVoting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveVoting> getAllActiveVotings() {
        return activeVotingRepository.findAllByFinishedDateIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoteAnswers> getUsersVote(User user, ActiveVoting activeVoting) {
        return voteAnswersRepository.findByActiveVotingAndUser(activeVoting, user);
    }

    @Override
    @Transactional
    public VoteAnswers saveVoteAnswers(VoteAnswers voteAnswers) {
        return voteAnswersRepository.save(voteAnswers);
    }
}
