package pl.miken.electionhandler.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.entity.VoteAnswers;
import pl.miken.electionhandler.repository.VoteAnswersRepository;
import pl.miken.electionhandler.service.VoteAnswersService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteAnswersServiceImpl implements VoteAnswersService {

    private final VoteAnswersRepository voteAnswersRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<VoteAnswers> findAnswerByVotingIdAndUserIdAndOptionId(Long votingId, Long userId, Long optionId) {
        return null;
    }
}
