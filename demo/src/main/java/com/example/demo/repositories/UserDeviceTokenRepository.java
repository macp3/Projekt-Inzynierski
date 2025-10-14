package com.example.demo.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.UserDeviceToken;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Integer> {

    List<UserDeviceToken> findByUserId(Integer userId);
}
