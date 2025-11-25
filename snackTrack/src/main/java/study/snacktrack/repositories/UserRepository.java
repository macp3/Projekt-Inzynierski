package study.snacktrack.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import study.snacktrack.entities.User;
import study.snacktrack.entities.enums.Status;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByPremiumExpirationNotNull();

    List<User> findByPremiumExpirationNull();

    Page<User> findAll(Pageable pageable);

    // Customowe wyszukiwanie po Emailu, Imieniu lub Nazwisku z Paginacją
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(String query, Pageable pageable);

    long countByStatus(Status status);

    long countByPremiumExpirationAfter(LocalDate date);
}
