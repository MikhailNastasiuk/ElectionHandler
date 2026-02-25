package pl.miken.electionhandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.miken.electionhandler.entity.Vote;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findAllByIsArchived(Boolean isArchived);

    Optional<Vote> findByUniqueIdentifierAndIsArchivedFalse(String uniqueIdentifier);
}
