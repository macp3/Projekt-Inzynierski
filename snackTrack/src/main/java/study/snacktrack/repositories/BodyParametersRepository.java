package study.snacktrack.repositories;

import java.util.Optional;

import study.snacktrack.entities.BodyParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyParametersRepository extends JpaRepository<BodyParameters, Integer> {

    Optional<BodyParameters> findByUserId(int userId);
    boolean existsByUserId(Integer userId);
}
