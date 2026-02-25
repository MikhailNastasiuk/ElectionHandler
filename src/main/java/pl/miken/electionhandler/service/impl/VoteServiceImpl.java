package pl.miken.electionhandler.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.miken.electionhandler.entity.Vote;
import pl.miken.electionhandler.repository.VoteRepository;
import pl.miken.electionhandler.service.VoteService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Vote> findByUniqueIdentifier(String uniqueIdentifier) {
        return voteRepository.findByUniqueIdentifierAndIsArchivedFalse(uniqueIdentifier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vote> findAllActiveVotes() {
        return voteRepository.findAllByIsArchived(false);
    }

    @Override
    @Transactional
    public Vote save(Vote vote) {
        return voteRepository.save(vote);
    }
}
