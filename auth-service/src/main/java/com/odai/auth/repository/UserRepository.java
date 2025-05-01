package com.odai.auth.repository;

import com.odai.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(UUID keycloakId);
    Optional<User> findByEmail(String email);
}
