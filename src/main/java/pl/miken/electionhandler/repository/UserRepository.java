package pl.miken.electionhandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.miken.electionhandler.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByIsActive(Boolean isActive);

    Optional<User> findByIdAndIsActiveTrue(Long id);

    Optional<User> findAllByPersonalNumber(String personalNumber);
}
