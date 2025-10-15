package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.TrainingInfo;

@Repository
public interface TrainingInfoRepository extends JpaRepository<TrainingInfo, Integer> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
