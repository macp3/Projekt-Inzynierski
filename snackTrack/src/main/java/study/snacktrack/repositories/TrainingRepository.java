package study.snacktrack.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {

    Optional<Training> findById(int trainingId);

    List<Training> findByTrainingId(int trainingId);

    boolean existsByTrainingIdAndExerciseIdAndDayOfExercise(int trainingId, int exerciseId, int dayOfExercise);
    List<Training> findAllByTrainingIdAndExerciseId(int trainingId, int exerciseId);
    void deleteAllByTrainingId(int trainingId);

    Optional<Training> findByTrainingIdAndExerciseIdAndDayOfExercise(int trainingId, int exerciseId, int dayOfExercise);

    List<Training> findAllByExerciseId(int exerciseId);
}
