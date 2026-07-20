package com.joseagim.traintracker.repository;

import com.joseagim.traintracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
