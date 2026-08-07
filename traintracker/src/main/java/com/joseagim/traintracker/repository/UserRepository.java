package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDni(String dni);

    Optional<User> findByPhoneNumber(String phoneNumber);

}
