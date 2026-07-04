package com.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Automatically generates SQL query: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}
