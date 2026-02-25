package pl.miken.electionhandler.service;

import pl.miken.electionhandler.entity.VoteAnswers;

import java.util.Optional;

public interface VoteAnswersService {

    Optional<VoteAnswers> findAnswerByVotingIdAndUserIdAndOptionId(Long votingId, Long userId, Long optionId);
}
