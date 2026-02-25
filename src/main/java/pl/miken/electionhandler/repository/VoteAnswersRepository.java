package pl.miken.electionhandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.miken.electionhandler.entity.ActiveVoting;
import pl.miken.electionhandler.entity.User;
import pl.miken.electionhandler.entity.VoteAnswers;

import java.util.Optional;

@Repository
public interface VoteAnswersRepository extends JpaRepository<VoteAnswers, Long> {

    Optional<VoteAnswers> findByActiveVotingAndUser(ActiveVoting activeVoting, User user);
}
