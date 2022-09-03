package com.stefan.springjwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.stefan.springjwt.models.User;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  @Query("SELECT u FROM User u WHERE u.resetCode = ?1")
  public User findByResetCode(String code);

  @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
  public User findByVerificationCode(String code);
  
  Optional<User> findByUsername(String username);
  
  User findByEmail(String email);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
