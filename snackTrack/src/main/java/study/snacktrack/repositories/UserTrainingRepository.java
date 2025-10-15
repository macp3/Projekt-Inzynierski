package study.snacktrack.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.UserTraining;

@Repository
public interface UserTrainingRepository extends JpaRepository<UserTraining, Integer> {

    List<UserTraining> findByUserId(int userId);
    //UserTraining findByUserId(int userId);
    void deleteAllByTrainingId(int trainingId);

}
