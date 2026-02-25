package pl.miken.electionhandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.Vote;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActiveVotingRepository extends JpaRepository<ActiveVoting, Long> {

    Optional<ActiveVoting> findByVoteAndFinishedDateIsNull(Vote vote);

    Optional<ActiveVoting> findByIdAndFinishedDateIsNull(Long id);

    List<ActiveVoting> findAllByFinishedDateIsNull();
}
