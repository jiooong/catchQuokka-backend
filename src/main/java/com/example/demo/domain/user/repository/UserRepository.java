package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // email을 통해 이미 생성된 사용자인지 처음 가입되는 사용자인지 판단
    Optional<User> findByEmail(String email);
}
