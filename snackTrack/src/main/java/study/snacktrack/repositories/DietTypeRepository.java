package study.snacktrack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.DietType;

@Repository
public interface DietTypeRepository extends JpaRepository<DietType, Integer> {

}
