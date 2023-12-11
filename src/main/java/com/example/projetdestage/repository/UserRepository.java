package com.example.projetdestage.repository;

import com.example.projetdestage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    Optional<User> findByOtp(String otp);


}
