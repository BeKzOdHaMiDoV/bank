package com.bank.bank.repository;


import com.bank.bank.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByTelegramId(Long telegramId);

    // Eager-загрузка аккаунта одним запросом
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.account WHERE u.id = :id")
    Optional<User> findByIdWithAccount(@Param("id") Long id);
}
