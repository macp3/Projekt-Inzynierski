package study.snacktrack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import study.snacktrack.entities.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
}
