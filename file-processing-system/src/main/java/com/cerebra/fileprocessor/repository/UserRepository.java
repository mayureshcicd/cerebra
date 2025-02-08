package com.cerebra.fileprocessor.repository;

import com.cerebra.fileprocessor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByEmail(String email);
    Optional<User> findByUserType(String userType);

}