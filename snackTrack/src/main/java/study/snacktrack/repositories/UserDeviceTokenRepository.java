package study.snacktrack.repositories;

import java.util.List;

import study.snacktrack.entities.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Integer> {

    List<UserDeviceToken> findByUserId(Integer userId);

    void deleteByDeviceToken(String deviceToken);
}
