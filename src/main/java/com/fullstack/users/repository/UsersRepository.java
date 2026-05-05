package com.fullstack.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.users.model.Users;

import java.util.Optional;
import java.util.UUID;

public class UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByCorreoIgnoreCase(String email);

    boolean existsByCorreoIgnoreCase(String email);
}
