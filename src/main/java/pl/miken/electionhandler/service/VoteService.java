package pl.miken.electionhandler.service;

import pl.miken.electionhandler.entity.Vote;

import java.util.List;
import java.util.Optional;

public interface VoteService {

    List<Vote> findAllActiveVotes();

    Optional<Vote> findByUniqueIdentifier(String uniqueIdentifier);

    Vote save(Vote vote);
}
