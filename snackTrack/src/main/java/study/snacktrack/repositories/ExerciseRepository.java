package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    boolean existsByName(String name);
    //List<Exercise> findAllByTrainingId(int trainingId);
}
